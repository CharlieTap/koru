package alloc

import kotlinx.cinterop.*
import liburing.iovec

value class VectorBuffer internal constructor(internal val vecs: CValuesRef<iovec>) {
    companion object Factory {
        fun alloc(n: Int): VectorBuffer = alloc(n, nativeHeap)

        internal fun alloc(n: Int, allocator: NativePlacement): VectorBuffer {
            return VectorBuffer(
                allocator.allocArray(n)
            )
        }
    }
}
