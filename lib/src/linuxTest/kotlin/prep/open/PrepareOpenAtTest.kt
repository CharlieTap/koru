package prep.open

import alloc.fakeHeapAllocator
import file.FileDescriptor
import kotlinx.cinterop.*
import liburing.io_uring_sqe
import queue.sqe.SubmissionQueueEntry
import file.FileDescriptorFlags
import file.FileMode
import kotlin.test.Test
import kotlin.test.assertEquals

internal class PrepareOpenAtTest {

    @Test
    fun `calls through to prepare openat successfully`() = memScoped {

        val sqe = alloc<io_uring_sqe>()
        val expectedSubmissionQueueEntry = SubmissionQueueEntry(sqe.ptr)
        val expectedDirFd = FileDescriptor.AT_FDCWD
        val expectedPath = "file.txt"
        val expectedPathPointer = alloc<ByteVar>()
        val expectedFlags = FileDescriptorFlags.Builder().build()
        val expectedMode = FileMode.Builder().build()

        val allocator = fakeHeapAllocator(listOf<NativePointed>(
            expectedPathPointer,
        ).iterator())

        val prepOpenAtInterface = object : PrepOpenAtInterface {
            override fun prepOpenAt(
                sqe: CValuesRef<io_uring_sqe>?,
                dirFd: Int,
                path: CValuesRef<ByteVar>?,
                flags: Int,
                mode: UInt
            ) {
                assertEquals(expectedSubmissionQueueEntry.sqe, sqe)
                assertEquals(expectedDirFd.fd, dirFd)
                assertEquals(expectedPathPointer.ptr, path)
                assertEquals(expectedFlags.flags, flags)
                assertEquals(expectedMode.mode, mode)
            }
        }

        val result = prepareOpenAt(
            expectedSubmissionQueueEntry,
            expectedDirFd,
            expectedPath,
            expectedFlags,
            expectedMode,
            prepOpenAtInterface,
            allocator,
        )

        assertEquals(Unit, result)
    }
}
