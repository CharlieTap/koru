package prep.close

import file.FileDescriptor
import queue.sqe.SubmissionQueueEntry

fun interface PrepareClose : (SubmissionQueueEntry, FileDescriptor) -> Unit

fun prepareClose(
    submissionQueueEntry: SubmissionQueueEntry,
    fileDescriptor: FileDescriptor
) = PrepareClose { _, _ ->
    prepareClose(submissionQueueEntry, fileDescriptor, IOUringCloseInterface)
}(submissionQueueEntry, fileDescriptor)

internal fun prepareClose(
    submissionQueueEntry: SubmissionQueueEntry,
    fileDescriptor: FileDescriptor,
    closeInterface: PrepCloseInterface
) {
    closeInterface.prepClose(submissionQueueEntry.sqe, fileDescriptor.fd)
}
