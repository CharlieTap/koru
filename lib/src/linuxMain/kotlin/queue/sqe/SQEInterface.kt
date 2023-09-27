package queue.sqe

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CValuesRef
import liburing.io_uring
import liburing.io_uring_get_sqe
import liburing.io_uring_sqe

internal interface SQEInterface {
    fun getSubmissionQueueEntry(ring: CValuesRef<io_uring>): CPointer<io_uring_sqe>?
}

internal object IOUringSQEInterface: SQEInterface {
    override fun getSubmissionQueueEntry(ring: CValuesRef<io_uring>): CPointer<io_uring_sqe>? =
        io_uring_get_sqe(ring)
}
