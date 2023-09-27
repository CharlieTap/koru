package prep.read

import alloc.VectorBuffer
import file.FileDescriptor
import kotlinx.cinterop.CValuesRef
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import liburing.io_uring_sqe
import liburing.iovec
import queue.sqe.SubmissionQueueEntry
import kotlin.test.Test
import kotlin.test.assertEquals

internal class PrepareReadVTest {

    @Test
    fun `calls through to prepare readv successfully`() = memScoped {

        val sqe = alloc<io_uring_sqe>()
        val expectedSubmissionQueueEntry = SubmissionQueueEntry(sqe.ptr)
        val expectedFileDescriptor = FileDescriptor(1)
        val expectedVectorBuffer = VectorBuffer.Factory.alloc(1024)
        val expectedVecsToRead = 1024
        val expectedOffset = 0L

        val prepReadVInterface = object : PrepReadVInterface {
            override fun prepReadV(
                sqe: CValuesRef<io_uring_sqe>?,
                fileDescriptor: Int,
                iovecs: CValuesRef<iovec>?,
                nrVecs: UInt,
                offset: ULong
            ) {
                assertEquals(expectedSubmissionQueueEntry.sqe, sqe)
                assertEquals(expectedFileDescriptor.fd, fileDescriptor)
                assertEquals(expectedVectorBuffer.vecs, iovecs)
                assertEquals(expectedVecsToRead.toUInt(), nrVecs)
                assertEquals(expectedOffset.toULong(), offset)
            }
        }

        val result = prepareReadV(
            expectedSubmissionQueueEntry,
            expectedFileDescriptor,
            expectedVectorBuffer,
            expectedVecsToRead,
            expectedOffset,
            prepReadVInterface
        )

        assertEquals(Unit, result)
    }
}
