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

internal class PrepareOpenAtDirectTest {

    @Test
    fun `calls through to prepare openat_direct successfully`() = memScoped {
        val sqe = alloc<io_uring_sqe>()
        val expectedSubmissionQueueEntry = SubmissionQueueEntry(sqe.ptr)
        val expectedDirFd = FileDescriptor.AT_FDCWD
        val expectedPath = "file.txt"
        val expectedPathPointer = alloc<ByteVar>()
        val expectedFlags = FileDescriptorFlags(0)
        val expectedMode = FileMode(0u)
        val expectedFileIndex = 1

        val allocator = fakeHeapAllocator(listOf<NativePointed>(
            expectedPathPointer,
        ).iterator())

        val prepOpenAtDirectInterface = object : PrepOpenAtDirectInterface {
            override fun prepOpenAtDirect(
                sqe: CValuesRef<io_uring_sqe>?,
                dirFd: Int,
                path: CValuesRef<ByteVar>?,
                flags: Int,
                mode: UInt,
                fileIndex: UInt
            ) {
                assertEquals(expectedSubmissionQueueEntry.sqe, sqe)
                assertEquals(expectedDirFd.fd, dirFd)
                assertEquals(expectedPathPointer.ptr, path)
                assertEquals(expectedFlags.flags, flags)
                assertEquals(expectedMode.mode, mode)
                assertEquals(expectedFileIndex.toUInt(), fileIndex)
            }
        }

        prepareOpenAtDirect(
            expectedSubmissionQueueEntry,
            expectedDirFd,
            expectedPath,
            expectedFlags,
            expectedMode,
            expectedFileIndex,
            prepOpenAtDirectInterface,
            allocator,
        )
    }
}
