package queue.sqe

import ring.Ring

fun interface GetSubmissionQueueEntry: (Ring) -> SubmissionQueueEntry?

fun getSubmissionQueueEntry(ring: Ring) = GetSubmissionQueueEntry {
    getSubmissionQueueEntry(it, IOUringSQEInterface)
}(ring)

internal fun getSubmissionQueueEntry(
    ring: Ring,
    sqeInterface: SQEInterface,
): SubmissionQueueEntry? {
    return sqeInterface.getSubmissionQueueEntry(ring.ring)?.let {
        SubmissionQueueEntry(it)
    }
}
