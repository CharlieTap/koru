package alloc

import kotlinx.cinterop.*

value class Buffer internal constructor(internal val buf: CArrayPointer<ByteVar>) {

    fun get(index: Int): Byte = memScoped {
        //todo
        return buf.getPointer(this)[index]
    }

    companion object Factory {
        fun alloc(size: Int = 1024): Buffer = alloc(size, nativeHeap)

        fun alloc(size: Int, allocator: NativePlacement): Buffer {
            return Buffer(
                allocator.allocArray(size),
            )
        }
    }

    override fun toString(): String {
        return buf.toKString()
    }
}
