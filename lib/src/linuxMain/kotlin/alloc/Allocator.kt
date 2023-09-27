package alloc

import kotlinx.cinterop.*

internal interface StackAllocator: NativePlacement
internal interface HeapAllocator {
    fun allocLongLived(size: Long, align: Int): NativePointed
    fun freeLongLived(pointer: NativePtr)
}

internal fun NativeFreeablePlacement.heapAllocator(): HeapAllocator = object : HeapAllocator {
    override fun allocLongLived(size: Long, align: Int): NativePointed = alloc(size, align)
    override fun freeLongLived(pointer: NativePtr) = free(pointer)
}

@ExperimentalForeignApi
internal inline fun <reified T : CVariable> HeapAllocator.allocLongLived(): T =
    allocLongLived(sizeOf<T>(), alignOf<T>()).reinterpret()

/**
 * An allocator that differentiates between allocations that we control the lifetime of
 * and those live beyond the scope of one or more bindings
 */
internal interface KoruAllocator: StackAllocator, HeapAllocator

/**
 *  For allocations that we control the lifetime of: force delegation to [MemScope] (basically stack alloc)
 *  For allocations we don't control the lifetime of: force delegation to injected "heap" allocator
*/
internal fun allocator(
    stackAllocator: MemScope,
    heapAllocator: NativeFreeablePlacement = nativeHeap,
): KoruAllocator {
    return object : NativePlacement by stackAllocator, KoruAllocator {
        override fun allocLongLived(size: Long, align: Int): NativePointed {
            return heapAllocator.alloc(size, align)
        }

        override fun freeLongLived(pointer: NativePtr) {
            heapAllocator.free(pointer)
        }
    }
}
