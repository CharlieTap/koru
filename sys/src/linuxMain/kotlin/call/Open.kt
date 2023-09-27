package call

import file.FileDescriptorFlags
import file.FileMode
import com.github.michaelbull.result.*
import platform.posix.*

sealed class OpenError {
    data object PermissionDenied : OpenError()
    data object FileExists : OpenError()
    data object TooManyFiles : OpenError()
    data object InvalidFlags : OpenError()
    data object PathTooLong : OpenError()
    data class Other(val error: Int) : OpenError()
}

private fun Int.openError() = when (this) {
    EACCES -> OpenError.PermissionDenied
    EEXIST -> OpenError.FileExists
    EMFILE -> OpenError.TooManyFiles
    EINVAL -> OpenError.InvalidFlags
    ENAMETOOLONG -> OpenError.PathTooLong
    else -> OpenError.Other(this)
}

fun sysOpen(
    path: String,
    flags: FileDescriptorFlags,
    mode: FileMode? = null
): Result<Int, OpenError> {

    val fd = mode?.let {
        open(path, flags.flags, mode.mode)
    } ?: open(path, flags.flags)

    return if (fd < 0) {
        Err(errno.openError())
    } else {
        Ok(fd)
    }
}
