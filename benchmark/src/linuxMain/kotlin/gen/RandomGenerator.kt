package gen

import call.sysClose
import call.sysOpen
import call.sysRead
import com.github.michaelbull.result.getOrThrow
import exceptions.OpenException
import exceptions.ReadException
import file.FileDescriptorFlags
import file.FileMode
import kotlinx.cinterop.*
import kotlin.math.abs

class RandomGenerator: () -> Int {

    companion object {
        private const val FILEPATH = "/dev/urandom"
    }

    private val fileFlags by lazy {
        FileDescriptorFlags.Builder().build()
    }

    private val fileMode by lazy {
        FileMode.Builder().build()
    }

    override fun invoke(): Int {

        val fd = sysOpen(FILEPATH, fileFlags, fileMode).getOrThrow {
            OpenException(FILEPATH, it)
        }

        val buffer = nativeHeap.allocArray<ByteVar>(4)

        sysRead(fd, buffer, 4).getOrThrow {
            ReadException(FILEPATH, it)
        }
        sysClose(fd)

        val randomNumber = buffer.reinterpret<IntVar>().pointed.value
        nativeHeap.free(buffer)

        return abs(randomNumber)
    }
}