package prep.close

import queue.sqe.SubmissionQueueEntry

fun interface PrepareCloseDirect : (SubmissionQueueEntry, Int) -> Unit

fun prepareCloseDirect(
    submissionQueueEntry: SubmissionQueueEntry,
    fileIndex: Int,
) = PrepareCloseDirect { _, _ ->
    prepareCloseDirect(submissionQueueEntry, fileIndex, IOUringCloseInterface)
}(submissionQueueEntry, fileIndex)

internal fun prepareCloseDirect(
    submissionQueueEntry: SubmissionQueueEntry,
    fileIndex: Int,
    closeInterface: PrepCloseDirectInterface
) {
    closeInterface.prepCloseDirect(submissionQueueEntry.sqe, fileIndex.toUInt())
}
