package gen

import call.sysClose
import call.sysOpen
import call.sysWrite
import com.github.michaelbull.result.getOrThrow
import file.FileDescriptorFlags
import file.FileMode
import kotlinx.cinterop.*
import platform.posix.*

class FileGenerator: (String, Long) -> String {
    companion object {
        const val FILE_GEN_DIRECTORY = "/workspace/gen/"

        val bufferCache = mutableMapOf<Long, CArrayPointer<ByteVar>>()
    }

    private fun fileBuffer(fileSize: Long): CArrayPointer<ByteVar> {
        return bufferCache[fileSize] ?: createFileBuffer(fileSize)
    }

    private fun createFileBuffer(fileSize: Long): CArrayPointer<ByteVar> {
        return nativeHeap.allocArray<ByteVar>(fileSize).apply {
            for (i in 0 until fileSize) {
                set(i, '1'.code.toByte())
            }
        }
    }

    private fun createDirIfNotExists(path: String) {
        path.split("/").reduce { acc, s ->
            val newPath = "$acc/$s"
            mkdir(newPath, 0b111_101_101.toUInt())
            newPath
        }
    }

    private fun createFile(path: String): Int {

        createDirIfNotExists(path.substringBeforeLast("/"))

        val flags = FileDescriptorFlags.Builder().apply {
            createIfNotExists()
            writeOnly()
        }.build()

        val mode = FileMode.Builder().build()
        val result = sysOpen(path, flags, mode)

        return result.getOrThrow {
            Exception("Failed to create file: $it")
        }
    }

    override fun invoke(fileName: String, fileSize: Long): String {
        val filePath = "$FILE_GEN_DIRECTORY$fileName"
        val fd = createFile(filePath)

        sysWrite(fd, fileBuffer(fileSize), fileSize).getOrThrow { error ->
            Exception("Failed to write buffer to file: $filePath $error")
        }

        sysClose(fd)
        return filePath
    }
}
