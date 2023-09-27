package prep.open

import alloc.HeapAllocator
import alloc.heapAllocator
import alloc.allocLongLived
import file.FileDescriptor
import kotlinx.cinterop.*
import queue.sqe.SubmissionQueueEntry

fun interface PrepareOpenAt2Direct: (SubmissionQueueEntry, FileDescriptor, String, OpenHow, Int) -> Unit

fun prepareOpenAt2Direct(
    submissionQueueEntry: SubmissionQueueEntry,
    dirFd: FileDescriptor,
    path: String,
    how: OpenHow,
    fileIndex: Int
) = PrepareOpenAt2Direct { _, _, _, _, _ ->
    memScoped {
        prepareOpenAt2Direct(
            submissionQueueEntry,
            dirFd,
            path,
            how,
            fileIndex,
            IOUringOpenInterface,
            nativeHeap.heapAllocator(),
        )
    }
}(submissionQueueEntry, dirFd, path, how, fileIndex)

internal fun prepareOpenAt2Direct(
    submissionQueueEntry: SubmissionQueueEntry,
    dirFd: FileDescriptor,
    path: String,
    how: OpenHow,
    fileIndex: Int,
    prepOpenAt2DirectInterface: PrepOpenAt2DirectInterface,
    allocator: HeapAllocator,
) {
    //todo basically leaking here 😢
    val pointer = allocator.allocLongLived<ByteVar>().ptr
    path.cstr.place(pointer)

    prepOpenAt2DirectInterface.prepOpenAt2Direct(
        submissionQueueEntry.sqe,
        dirFd.fd,
        pointer,
        how.toNative(allocator),
        fileIndex.toUInt()
    )
}
