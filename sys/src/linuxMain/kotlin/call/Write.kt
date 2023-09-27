package call

import com.github.michaelbull.result.*
import kotlinx.cinterop.*
import platform.posix.*

sealed class WriteError {
    data object BadFileDescriptor : WriteError()
    data object InvalidArgument : WriteError()
    data object PermissionDenied : WriteError()
    data object NoSpaceLeftOnDevice : WriteError()
    data object Interrupted : WriteError()
    data class Other(val error: Int) : WriteError()
}

private fun Int.writeError() = when (this) {
    EBADF -> WriteError.BadFileDescriptor
    EINVAL -> WriteError.InvalidArgument
    EPERM -> WriteError.PermissionDenied
    ENOSPC -> WriteError.NoSpaceLeftOnDevice
    EINTR -> WriteError.Interrupted
    else -> WriteError.Other(this)
}

fun sysWrite(
    fileDescriptor: Int,
    buffer: CArrayPointer<ByteVar>,
    bytesToWrite: Long,
): Result<Long, WriteError> {

    var activeBuffer = buffer
    var bytesLeftToWrite = bytesToWrite
    var totalBytesWritten = 0L

    while (true) {
        val bytesWritten = write(fileDescriptor, activeBuffer, bytesLeftToWrite.toULong())

        if (bytesWritten == -1L) {
            val error = errno.writeError()
            if (error is WriteError.Interrupted) {
                continue
            }
            return Err(error)
        }

        bytesLeftToWrite -= bytesWritten
        totalBytesWritten += bytesWritten

        if (bytesWritten == 0L || bytesLeftToWrite == 0L) {
            break
        }

        activeBuffer = activeBuffer.plus(bytesWritten)!!
    }

    return Ok(totalBytesWritten)
}
