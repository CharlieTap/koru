package prep.read

import alloc.Buffer
import file.FileDescriptor
import queue.sqe.SubmissionQueueEntry

fun interface PrepareRead: (SubmissionQueueEntry, FileDescriptor, Buffer, Int, Long) -> Unit

fun prepareRead(
    submissionQueueEntry: SubmissionQueueEntry,
    fileDescriptor: FileDescriptor,
    buffer: Buffer,
    bytesToRead: Int,
    offset: Long,
) = PrepareRead { _, _, _, _, _->
    prepareRead(
        submissionQueueEntry,
        fileDescriptor,
        buffer,
        bytesToRead,
        offset,
        IOUringReadInterface
    )
}(submissionQueueEntry, fileDescriptor, buffer, bytesToRead, offset)

internal fun prepareRead(
    submissionQueueEntry: SubmissionQueueEntry,
    fileDescriptor: FileDescriptor,
    buffer: Buffer,
    bytesToRead: Int,
    offset: Long,
    prepReadInterface: PrepReadInterface,
) {
    prepReadInterface.prepRead(
        submissionQueueEntry.sqe,
        fileDescriptor.fd,
        buffer.buf,
        bytesToRead.toUInt(),
        offset.toULong(),
    )
}