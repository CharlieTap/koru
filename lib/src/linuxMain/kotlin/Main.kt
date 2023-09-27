
import alloc.Buffer
import call.sysClose
import call.sysOpen
import call.sysRead
import com.github.michaelbull.result.getOrThrow
import file.FileDescriptor
import file.FileDescriptorFlags
import file.FileMode
import init.InitRingResult
import init.RingEntries
import init.initRing
import kotlinx.cinterop.memScoped
import prep.close.prepareCloseDirect
import prep.open.prepareOpenAtDirect
import prep.read.prepareRead
import queue.cqe.markCompletionQueueEntrySeen
import queue.cqe.waitCompletionQueueEntry
import queue.sqe.getSubmissionQueueEntry
import register.RegisterDirectFilesResult
import register.registerDirectFiles
import ring.submitRing
import kotlin.time.TimeSource
import kotlin.time.measureTime


fun read(fileDescriptor: FileDescriptor, buffer: Buffer, bytesToRead: Int): Int 

fun readBlocking(buffer: Buffer) = memScoped {
    val test = sysOpen("/workspace/koru/test3.txt",
        FileDescriptorFlags.Builder().build(),
        FileMode.Builder().build()).getOrThrow { Exception("failed to open") }

    sysRead(test, buffer.buf, 1028)
    sysClose(test)
}

fun main() = memScoped {

    val buffer = Buffer.alloc(1028, this)

    val blockingDuration = measureTime {
        readBlocking(buffer)
    }


    val setupTimemark = TimeSource.Monotonic.markNow()
    val ring = when(val initResult = initRing(RingEntries(32u))) {
        is InitRingResult.Failure -> throw IllegalStateException(initResult.toString())
        is InitRingResult.Success -> {
            initResult.ring
        }
    }
    val ringInitDuration = setupTimemark.elapsedNow()

    val registerDirectFilesTimemark = TimeSource.Monotonic.markNow()
    registerDirectFiles(ring, 10).also { registerResult ->
        when(registerResult) {
            is RegisterDirectFilesResult.Failure -> throw IllegalStateException("Failed to register files")
            else -> Unit
        }
    }
    val registerDirectFilesDuration = registerDirectFilesTimemark.elapsedNow()

    val sqePrepTimemark = TimeSource.Monotonic.markNow()

    val openSqe = getSubmissionQueueEntry(ring)!!
    prepareOpenAtDirect(
        openSqe,
        FileDescriptor.AT_FDCWD,
        "/workspace/koru/test3.txt",
        FileDescriptorFlags.Builder().readOnly().build(),
        FileMode.Builder().build(),
        0
    )
    openSqe.apply {
        setUserData(1)
        setLinked()
    }

    val readSqe = getSubmissionQueueEntry(ring)!!

    prepareRead(
        readSqe,
        FileDescriptor(0),
        buffer,
        1028,
        0
    )
    readSqe.apply {
        setUserData(2)
        setFixedFile()
        setLinked()
    }

    val closeSqe = getSubmissionQueueEntry(ring)!!
    prepareCloseDirect(closeSqe, 0)
    closeSqe.apply {
        setUserData(3)
    }

    val sqePrepDuration = sqePrepTimemark.elapsedNow()


    val uringDuration = measureTime {
        submitRing(ring)
    }

    val reapDuration = measureTime {
        repeat(3) {
            val cqe = waitCompletionQueueEntry(ring)
            cqe?.let {
                markCompletionQueueEntrySeen(ring, cqe)
            }
        }
    }


    println("blocking duration:$blockingDuration")
    println("init duration:$ringInitDuration")
    println("register files duration:$registerDirectFilesDuration")
    println("prep duration:$sqePrepDuration")
    println("submit duration:$uringDuration")
    println("reap duration:$reapDuration")

    Unit
}
