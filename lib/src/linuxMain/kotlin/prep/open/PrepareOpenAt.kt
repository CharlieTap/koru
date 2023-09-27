package prep.open

import alloc.HeapAllocator
import alloc.allocLongLived
import alloc.heapAllocator
import file.FileDescriptor
import kotlinx.cinterop.*
import queue.sqe.SubmissionQueueEntry
import file.FileDescriptorFlags
import file.FileMode

fun interface PrepareOpenAt: (SubmissionQueueEntry, FileDescriptor, String, FileDescriptorFlags, FileMode) -> Unit

fun prepareOpenAt(
    submissionQueueEntry: SubmissionQueueEntry,
    dirFd: FileDescriptor,
    path: String,
    flags: FileDescriptorFlags,
    mode: FileMode
) = PrepareOpenAt { _, _, _, _, _ ->
    memScoped {
        prepareOpenAt(
            submissionQueueEntry,
            dirFd,
            path,
            flags,
            mode,
            IOUringOpenInterface,
            nativeHeap.heapAllocator(),
        )
    }
}(submissionQueueEntry, dirFd, path, flags, mode)

internal fun prepareOpenAt(
    submissionQueueEntry: SubmissionQueueEntry,
    dirFd: FileDescriptor,
    path: String,
    flags: FileDescriptorFlags,
    mode: FileMode,
    prepOpenAtInterface: PrepOpenAtInterface,
    allocator: HeapAllocator,
) {
    //todo basically leaking here 😢
    val pointer = allocator.allocLongLived<ByteVar>().ptr
    path.cstr.place(pointer)

    prepOpenAtInterface.prepOpenAt(
        submissionQueueEntry.sqe,
        dirFd.fd,
        pointer,
        flags.flags,
        mode.mode
    )
}
