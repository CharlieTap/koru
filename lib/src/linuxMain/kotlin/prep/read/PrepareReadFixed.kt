package prep.read

import alloc.Buffer
import file.FileDescriptor
import queue.sqe.SubmissionQueueEntry

fun interface PrepareReadFixed: (SubmissionQueueEntry, FileDescriptor, Buffer, Int, Long, Int) -> Unit

fun prepareReadFixed(
    submissionQueueEntry: SubmissionQueueEntry,
    fileDescriptor: FileDescriptor,
    buffer: Buffer,
    bytesToRead: Int,
    offset: Long,
    bufIndex: Int
) = PrepareReadFixed { _, _, _, _, _, _ ->
    prepareReadFixed(
        submissionQueueEntry,
        fileDescriptor,
        buffer,
        bytesToRead,
        offset,
        bufIndex,
        IOUringReadInterface
    )
}(submissionQueueEntry, fileDescriptor, buffer, bytesToRead, offset, bufIndex)

internal fun prepareReadFixed(
    submissionQueueEntry: SubmissionQueueEntry,
    fileDescriptor: FileDescriptor,
    buffer: Buffer,
    bytesToRead: Int,
    offset: Long,
    bufIndex: Int,
    prepReadFixedInterface: PrepReadFixedInterface,
) {
    prepReadFixedInterface.prepReadFixed(
        submissionQueueEntry.sqe,
        fileDescriptor.fd,
        buffer.buf,
        bytesToRead.toUInt(),
        offset.toULong(),
        bufIndex
    )
}
