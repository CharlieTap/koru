package alloc

import kotlinx.cinterop.*

/**
 * During tests all heap allocations should just be forwarded to the [MemScope] of the test
 */
internal fun MemScope.testAllocator() = allocator(
    this,
    object : NativeFreeablePlacement {
        override fun alloc(size: Long, align: Int): NativePointed = this@testAllocator.alloc(size, align)
        override fun free(mem: NativePtr) = Unit
    }
)

internal fun fakeStackAllocator(
    stackAllocations: Iterator<NativePointed> = emptyList<NativePointed>().iterator(),
): StackAllocator = object : StackAllocator {
    override fun alloc(size: Long, align: Int): NativePointed {
        return stackAllocations.next()
    }
}

internal fun fakeHeapAllocator(
    heapAllocations: Iterator<NativePointed> = emptyList<NativePointed>().iterator(),
): HeapAllocator = object : HeapAllocator {
    @Suppress("UNCHECKED_CAST")
    override fun allocLongLived(size: Long, align: Int): NativePointed {
        return heapAllocations.next()
    }

    override fun freeLongLived(pointer: NativePtr)  = Unit
}

internal fun fakeAllocator(
    stackAllocations: Iterator<NativePointed> = emptyList<NativePointed>().iterator(),
    heapAllocations: Iterator<NativePointed> = emptyList<NativePointed>().iterator(),
): KoruAllocator {
    return object :
        StackAllocator by fakeStackAllocator(stackAllocations),
        HeapAllocator by fakeHeapAllocator(heapAllocations),
        KoruAllocator{}
}
