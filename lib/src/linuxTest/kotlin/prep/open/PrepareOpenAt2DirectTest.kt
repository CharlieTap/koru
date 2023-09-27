package prep.open

import alloc.fakeHeapAllocator
import file.FileDescriptor
import kotlinx.cinterop.*
import liburing.io_uring_sqe
import liburing.open_how
import queue.sqe.SubmissionQueueEntry
import file.FileDescriptorFlags
import file.FileMode
import kotlin.test.Test
import kotlin.test.assertEquals

internal class PrepareOpenAt2DirectTest {

    @Test
    fun `calls through to prepare openat2_direct successfully`() = memScoped {
        val sqe = alloc<io_uring_sqe>()
        val expectedSubmissionQueueEntry = SubmissionQueueEntry(sqe.ptr)
        val expectedDirFd = FileDescriptor.AT_FDCWD
        val expectedPath = "file.txt"
        val expectedPathPointer = alloc<ByteVar>()
        val expectedOpenHowPointer = alloc<open_how>()
        val expectedHow = OpenHow(
            fileDescriptorFlags = FileDescriptorFlags(0),
            fileMode = FileMode(0u),
            resolve = 0u
        )
        val expectedFileIndex = 1

        val allocator = fakeHeapAllocator(listOf<NativePointed>(
            expectedPathPointer,
            expectedOpenHowPointer,
        ).iterator())

        val prepOpenAt2DirectInterface = object : PrepOpenAt2DirectInterface {
            override fun prepOpenAt2Direct(
                sqe: CValuesRef<io_uring_sqe>?,
                dirFd: Int,
                path: CValuesRef<ByteVar>?,
                how: CValuesRef<open_how>?,
                fileIndex: UInt
            ) {
                assertEquals(expectedSubmissionQueueEntry.sqe, sqe)
                assertEquals(expectedDirFd.fd, dirFd)
                assertEquals(expectedPathPointer.ptr, path)
                assertEquals(expectedOpenHowPointer.ptr, how)
                assertEquals(expectedFileIndex.toUInt(), fileIndex)

                how?.let {
                    val openHow = it.getPointer(this@memScoped).pointed
                    assertEquals(expectedHow.fileDescriptorFlags.flags.toULong(), openHow.flags)
                    assertEquals(expectedHow.fileMode.mode.toULong(), openHow.mode)
                    assertEquals(expectedHow.resolve, openHow.resolve)
                }
            }
        }

        prepareOpenAt2Direct(
            expectedSubmissionQueueEntry,
            expectedDirFd,
            expectedPath,
            expectedHow,
            expectedFileIndex,
            prepOpenAt2DirectInterface,
            allocator,
        )
    }
}
