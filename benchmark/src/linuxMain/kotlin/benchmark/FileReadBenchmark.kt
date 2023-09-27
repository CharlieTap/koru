package benchmark

import call.sysClose
import call.sysOpen
import call.sysRead
import com.github.michaelbull.result.getOrThrow
import file.FileDescriptorFlags
import gen.*
import kotlinx.cinterop.*
import kotlin.time.TimeSource
import platform.posix.free
import posix.aioRead

class FileReadBenchmark(
    private val fileGenerator: FileGenerator = FileGenerator(),
    private val randomGenerator: RandomGenerator = RandomGenerator(),
    private val bufferGenerator: BufferGenerator = MemAlignedBufferGenerator()
): (FileDescriptorFlags) -> Map<Long, BenchmarkResult> {

    companion object {
        private val BUFFER_SIZES = arrayOf(
            1024,
            2048,
            4096L,
            8192L,
            16384L,
            32768L,
            65536L,
            131072L,
            262144L,
        )
    }

    private val fileSize by lazy {
        BUFFER_SIZES.max()
    }


    private fun benchmarkRead(
        filePath: String,
        fileDescriptorFlags: FileDescriptorFlags,
        fileDescriptor: Int,
        buffer: CArrayPointer<ByteVar>,
        bytesToRead: Long,
        bufferCapacity: Long,
    ): BenchmarkResult {
        val timeMark = TimeSource.Monotonic.markNow()

        var firstLoop = true
        var syscallTotal = 1
        var totalBytesRead = 0L

        while(true) {

            val bytesRead = if(fileDescriptorFlags.isNonBlocking()) {
                aioRead(fileDescriptor, buffer, bytesToRead)
            } else {
                sysRead(fileDescriptor, buffer, bytesToRead)
            }.getOrThrow {
                Exception("Failed to read from file $filePath $it")
            }

            if(firstLoop) {
                firstLoop = false
            } else {
                syscallTotal++
            }

            totalBytesRead += bytesRead

            if(bytesRead == 0L || totalBytesRead == bufferCapacity) break
        }

        return BenchmarkResult(
            bytesToRead,
            fileDescriptorFlags,
            syscallTotal,
            timeMark.elapsedNow(),
        )
    }

    private fun createRandomFile(): String {
        val random = randomGenerator()
        return fileGenerator.invoke("$random.txt", fileSize)
    }

    override fun invoke(flags: FileDescriptorFlags): Map<Long, BenchmarkResult> {

        val result = mutableMapOf<Long, BenchmarkResult>()
        val filePath = createRandomFile()

        BUFFER_SIZES.forEach { bufferSize ->
            val fileDescriptor = sysOpen(filePath, flags).getOrThrow {
                Exception("Failed to open file: $it with flags: $flags")
            }

            val resolvedBufferSize = if(flags.isDirect()) {
                fileSize
            } else {
                bufferSize
            }

            val buffer = bufferGenerator.invoke(filePath, BufferSize(resolvedBufferSize))

            val benchmarkResult = benchmarkRead(filePath, flags, fileDescriptor, buffer, resolvedBufferSize, resolvedBufferSize)
            result[resolvedBufferSize] = benchmarkResult

            free(buffer)
            sysClose(fileDescriptor)
        }

        return result
    }
}

