package gen

import com.github.michaelbull.result.getOrThrow
import ffi.getDeviceBlockSize
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CArrayPointer
import kotlinx.cinterop.reinterpret
import lib.aligned_alloc

value class BufferSize(val size: Long)
fun interface BufferGenerator: (String, BufferSize) -> CArrayPointer<ByteVar>

class MemAlignedBufferGenerator: BufferGenerator {

    override fun invoke(filePath: String, bufferSize: BufferSize): CArrayPointer<ByteVar> {
        val blockSize = getDeviceBlockSize(filePath).getOrThrow {
            Exception("Failed to retrieve block-size from filepath: $filePath")
        }

        return aligned_alloc(
            blockSize,
            bufferSize.size.toULong()
        )?.reinterpret<ByteVar>()!!
    }

}