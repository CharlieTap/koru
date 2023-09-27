package prep.open

import alloc.HeapAllocator
import alloc.heapAllocator
import alloc.allocLongLived
import file.FileDescriptor
import kotlinx.cinterop.ByteVar
import queue.sqe.SubmissionQueueEntry
import file.FileDescriptorFlags
import file.FileMode
import kotlinx.cinterop.cstr
import kotlinx.cinterop.nativeHeap
import kotlinx.cinterop.*

fun interface PrepareOpenAtDirect : (SubmissionQueueEntry, FileDescriptor, String, FileDescriptorFlags, FileMode, Int) -> Unit

fun prepareOpenAtDirect(
    submissionQueueEntry: SubmissionQueueEntry,
    dirFd: FileDescriptor,
    path: String,
    flags: FileDescriptorFlags,
    mode: FileMode,
    fileIndex: Int
) = PrepareOpenAtDirect { _, _, _, _, _, _ ->
    prepareOpenAtDirect(
        submissionQueueEntry,
        dirFd,
        path,
        flags,
        mode,
        fileIndex,
        IOUringOpenInterface,
        nativeHeap.heapAllocator(),
    )
}( submissionQueueEntry, dirFd, path, flags, mode, fileIndex)

internal fun prepareOpenAtDirect(
    submissionQueueEntry: SubmissionQueueEntry,
    dirFd: FileDescriptor,
    path: String,
    flags: FileDescriptorFlags,
    mode: FileMode,
    fileIndex: Int,
    prepOpenAtDirectInterface: PrepOpenAtDirectInterface,
    allocator: HeapAllocator,
) {
    //todo basically leaking here 😢
    val pointer = allocator.allocLongLived<ByteVar>().ptr
    path.cstr.place(pointer)

    prepOpenAtDirectInterface.prepOpenAtDirect(
        submissionQueueEntry.sqe,
        dirFd.fd,
        pointer,
        flags.flags,
        mode.mode,
        fileIndex.toUInt()
    )
}
