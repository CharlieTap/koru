package prep.read

import alloc.Buffer
import file.FileDescriptor
import kotlinx.cinterop.CValuesRef
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import liburing.io_uring_sqe
import queue.sqe.SubmissionQueueEntry
import kotlin.test.Test
import kotlin.test.assertEquals

internal class PrepareReadFixedTest {

    @Test
    fun `calls through to prepare read fixed successfully`() = memScoped {

        val sqe = alloc<io_uring_sqe>()
        val expectedSubmissionQueueEntry = SubmissionQueueEntry(sqe.ptr)
        val expectedFileDescriptor = FileDescriptor(1)
        val expectedBuffer = Buffer.Factory.alloc(1024, this)
        val expectedBytesToRead = 1024
        val expectedOffset = 0L
        val expectedBufIndex = 0

        val prepReadFixedInterface = object : PrepReadFixedInterface {
            override fun prepReadFixed(
                sqe: CValuesRef<io_uring_sqe>?,
                fileDescriptor: Int,
                buffer: CValuesRef<*>?,
                bytesToRead: UInt,
                offset: ULong,
                bufIndex: Int
            ) {
                assertEquals(expectedSubmissionQueueEntry.sqe, sqe)
                assertEquals(expectedFileDescriptor.fd, fileDescriptor)
                assertEquals(expectedBuffer.buf, buffer)
                assertEquals(expectedBytesToRead.toUInt(), bytesToRead)
                assertEquals(expectedOffset.toULong(), offset)
                assertEquals(expectedBufIndex, bufIndex)
            }
        }

        val result = prepareReadFixed(
            expectedSubmissionQueueEntry,
            expectedFileDescriptor,
            expectedBuffer,
            expectedBytesToRead,
            expectedOffset,
            expectedBufIndex,
            prepReadFixedInterface
        )

        assertEquals(Unit, result)
    }
}
