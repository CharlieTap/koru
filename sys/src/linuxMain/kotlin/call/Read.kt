package call

import com.github.michaelbull.result.*
import kotlinx.cinterop.*
import platform.posix.*

sealed class ReadError {
    data object BadFileDescriptor : ReadError()
    data object InvalidArgument : ReadError()
    data object FileTooLarge : ReadError()
    data object Interrupted : ReadError()
    data object PermissionDenied : ReadError()
    data object WouldBlock : ReadError()
    data class Other(val error: Int) : ReadError()
}

private fun Int.readError() = when(this) {
    EBADF -> ReadError.BadFileDescriptor
    EINVAL -> ReadError.InvalidArgument
    EFBIG -> ReadError.FileTooLarge
    EINTR -> ReadError.Interrupted
    EPERM -> ReadError.PermissionDenied
    EAGAIN -> ReadError.WouldBlock
    else -> ReadError.Other(this)
}

fun sysRead(
    fileDescriptor: Int,
    buffer: CArrayPointer<ByteVar>,
    bytesToRead: Long,
): Result<Long, ReadError> {

    var activeBuffer = buffer
    var bytesLeftToRead = bytesToRead
    var totalBytesRead = 0L

    while(true) {
        val bytesRead = read(fileDescriptor, activeBuffer, bytesLeftToRead.toULong())

        if(bytesRead == -1L) {
            when(val error = errno.readError()) {
                is ReadError.Interrupted -> continue
                is ReadError.WouldBlock -> {
                    // This would only happen if reading from a fd of a
                    // non-regular file, i.e. socket, pipe
                    usleep(10u)
                    continue
                }
                else -> return Err(error)
            }
        }

        bytesLeftToRead -= bytesRead
        totalBytesRead += bytesRead

        if(bytesRead == 0L || bytesLeftToRead == 0L) {
            break
        }

        activeBuffer = activeBuffer.plus(bytesRead)!!
    }

    return Ok(totalBytesRead)
}
