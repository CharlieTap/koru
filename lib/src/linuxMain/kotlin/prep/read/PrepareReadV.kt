package prep.read

import alloc.VectorBuffer
import file.FileDescriptor
import queue.sqe.SubmissionQueueEntry

fun interface PrepareReadV: (SubmissionQueueEntry, FileDescriptor, VectorBuffer, Int, Long) -> Unit

fun prepareReadV(
    submissionQueueEntry: SubmissionQueueEntry,
    fileDescriptor: FileDescriptor,
    vectorBuffer: VectorBuffer,
    vecsToRead: Int,
    offset: Long
) = PrepareReadV { _, _, _, _, _ ->
    prepareReadV(
        submissionQueueEntry,
        fileDescriptor,
        vectorBuffer,
        vecsToRead,
        offset,
        IOUringReadInterface
    )
}( submissionQueueEntry, fileDescriptor, vectorBuffer, vecsToRead, offset)

internal fun prepareReadV(
    submissionQueueEntry: SubmissionQueueEntry,
    fileDescriptor: FileDescriptor,
    vectorBuffer: VectorBuffer,
    vecsToRead: Int,
    offset: Long,
    prepReadVInterface: PrepReadVInterface,
) {
    prepReadVInterface.prepReadV(
        submissionQueueEntry.sqe,
        fileDescriptor.fd,
        vectorBuffer.vecs,
        vecsToRead.toUInt(),
        offset.toULong()
    )
}
