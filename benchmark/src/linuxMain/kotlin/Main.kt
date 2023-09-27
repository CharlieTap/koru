
import alloc.Buffer
import file.FileDescriptor
import file.FileDescriptorFlags
import file.FileMode
import init.InitRingResult
import init.RingEntries
import init.initRing
import kotlinx.cinterop.*
import prep.open.prepareOpenAtDirect
import prep.read.prepareRead
import probe.probeSupportedOpCodes
import queue.cqe.markCompletionQueueEntrySeen
import queue.cqe.waitCompletionQueueEntry
import queue.sqe.getSubmissionQueueEntry
import register.RegisterDirectFilesResult
import register.registerDirectFiles
import ring.submitRing


fun main() = memScoped {

    val result = probeSupportedOpCodes()

    println(result)

    val ring = when(val initResult = initRing(RingEntries(32u))) {
        is InitRingResult.Failure -> throw IllegalStateException(initResult.toString())
        is InitRingResult.Success -> {
            println(initResult.details)
            initResult.ring
        }
    }

    registerDirectFiles(ring, 10).also { registerResult ->
        when(registerResult) {
            is RegisterDirectFilesResult.Failure -> throw IllegalStateException("Failed to register files")
            else -> Unit
        }
    }

    val buffer = Buffer.Factory.alloc(1028)


//    val test = sysOpen("/workspace/koru/test3.txt",
//        FileDescriptorFlags.Builder().build(),
//        FileMode.Builder().build()).getOrThrow { Exception("failed to open") }

//    sysRead(test, buffer.buf, 1028)

//    val IORING_FILE_INDEX_ALLOC: UInt = 0xFFFFFFFFu

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
    }



    println(openSqe)
    submitRing(ring)

    repeat(2) {
        val cqe = waitCompletionQueueEntry(ring)
        cqe?.let {
            println(it)
            markCompletionQueueEntrySeen(ring, cqe)
        }
    }

    println(buffer)

}
