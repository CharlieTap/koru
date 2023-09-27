package queue.sqe

import kotlinx.cinterop.*
import liburing.io_uring
import liburing.io_uring_sqe
import queue.sqe.SQEInterface
import queue.sqe.SubmissionQueueEntry
import queue.sqe.getSubmissionQueueEntry
import ring.Ring
import kotlin.test.Test
import kotlin.test.assertEquals

internal class GetSubmissionQueueEntryTest {

    @Test
    fun `get a submission queue entry successfully`() = memScoped {

        val sqe = alloc<io_uring_sqe>()

        val fakeInterface = object: SQEInterface {
            override fun getSubmissionQueueEntry(ring: CValuesRef<io_uring>): CPointer<io_uring_sqe>? {
                return sqe.ptr
            }
        }

        val ring = Ring(alloc<io_uring>().ptr)
        val result = getSubmissionQueueEntry(ring, fakeInterface)

        assertEquals(SubmissionQueueEntry(sqe.ptr), result)
    }

    @Test
    fun `failure to get sqe returns null`() = memScoped {

        val fakeInterface = object: SQEInterface {
            override fun getSubmissionQueueEntry(ring: CValuesRef<io_uring>): CPointer<io_uring_sqe>? {
                return null
            }
        }

        val ring = Ring(alloc<io_uring>().ptr)
        val result = getSubmissionQueueEntry(ring, fakeInterface)

        assertEquals(null, result)
    }
}
