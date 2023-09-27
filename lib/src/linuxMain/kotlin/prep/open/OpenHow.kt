package prep.open

import alloc.HeapAllocator
import alloc.allocLongLived
import file.FileDescriptorFlags
import file.FileMode
import kotlinx.cinterop.*
import liburing.open_how

data class OpenHow(
    val fileDescriptorFlags: FileDescriptorFlags,
    val fileMode: FileMode,
    val resolve: ULong
) {
    internal fun toNative(allocator: HeapAllocator): CValuesRef<open_how> {
        return allocator.allocLongLived<open_how>().apply {
            flags = fileDescriptorFlags.flags.toULong()
            mode = fileMode.mode.toULong()
            resolve = resolve
        }.ptr
    }
}