package prep.open

import alloc.HeapAllocator
import alloc.allocLongLived
import alloc.heapAllocator
import file.FileDescriptor
import kotlinx.cinterop.*
import queue.sqe.SubmissionQueueEntry

fun interface PrepareOpenAt2: (SubmissionQueueEntry, FileDescriptor, String, OpenHow) -> Unit

fun prepareOpenAt2(
    submissionQueueEntry: SubmissionQueueEntry,
    dirFd: FileDescriptor,
    path: String,
    how: OpenHow
) = PrepareOpenAt2 { _, _, _, _ ->
    memScoped {
        prepareOpenAt2(
            submissionQueueEntry,
            dirFd,
            path,
            how,
            IOUringOpenInterface,
            nativeHeap.heapAllocator(),
        )
    }
}(submissionQueueEntry, dirFd, path, how)

internal fun prepareOpenAt2(
    submissionQueueEntry: SubmissionQueueEntry,
    dirFd: FileDescriptor,
    path: String,
    how: OpenHow,
    prepOpenAt2Interface: PrepOpenAt2Interface,
    allocator: HeapAllocator,
) {
    //todo basically leaking here 😢
    val pointer = allocator.allocLongLived<ByteVar>().ptr
    path.cstr.place(pointer)

    prepOpenAt2Interface.prepOpenAt2(
        submissionQueueEntry.sqe,
        dirFd.fd,
        pointer,
        how.toNative(allocator)
    )
}