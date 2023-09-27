package call

import com.github.michaelbull.result.*
import platform.posix.*

sealed class CloseError {
    data object BadFileDescriptor : CloseError()
    data object Interrupted : CloseError()
    data class Other(val error: Int) : CloseError()
}

private fun Int.closeError() = when (this) {
    EBADF -> CloseError.BadFileDescriptor
    EINTR -> CloseError.Interrupted
    else -> CloseError.Other(this)
}

fun sysClose(fileDescriptor: Int): Result<Unit, CloseError> {
    while (true) {
        val result = close(fileDescriptor)
        return if (result == 0) {
            Ok(Unit)
        } else {
            val error = errno.closeError()
            if (error is CloseError.Interrupted) {
                continue
            }
            Err(error)
        }
    }
}
