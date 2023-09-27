package prep.close

import file.FileDescriptor
import kotlinx.cinterop.CValuesRef
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import liburing.io_uring_sqe
import queue.sqe.SubmissionQueueEntry
import kotlin.test.Test
import kotlin.test.assertEquals

internal class PrepareCloseTest {

    @Test
    fun `calls through to prepare close successfully`() = memScoped {
        val sqe = alloc<io_uring_sqe>()
        val expectedSubmissionQueueEntry = SubmissionQueueEntry(sqe.ptr)
        val expectedFileDescriptor = FileDescriptor.AT_FDCWD

        val closeInterface = object : PrepCloseInterface {
            override fun prepClose(sqe: CValuesRef<io_uring_sqe>, fileDescriptor: Int) {
                assertEquals(expectedSubmissionQueueEntry.sqe, sqe)
                assertEquals(expectedFileDescriptor.fd, fileDescriptor)
            }
        }

        prepareClose(
            expectedSubmissionQueueEntry,
            expectedFileDescriptor,
            closeInterface
        )
    }
}
