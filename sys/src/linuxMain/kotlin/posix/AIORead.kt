package posix

import com.github.michaelbull.result.*
import kotlinx.cinterop.*
import platform.linux.aio_read
import platform.linux.aio_error
import platform.linux.aio_return
import platform.linux.aiocb
import platform.posix.*

sealed class AioReadError {
    data object BadFileDescriptor : AioReadError()
    data object InvalidArgument : AioReadError()
    data object OperationNotSupported : AioReadError()
    data object Interrupted : AioReadError()
    data class Other(val error: Int) : AioReadError()
}

private fun Int.aioReadError() = when (this) {
    EBADF -> AioReadError.BadFileDescriptor
    EINVAL -> AioReadError.InvalidArgument
    ENOSYS -> AioReadError.OperationNotSupported
    EINTR -> AioReadError.Interrupted
    else -> AioReadError.Other(this)
}

/**
 * This isn't actually a syscall, it just avoids blocking by spawning
 * a user space thread behind the scenes
 */
fun aioRead(
    fileDescriptor: Int,
    buffer: CArrayPointer<ByteVar>,
    bytesToRead: Long,
    offset: Long = 0L
): Result<Long, AioReadError> {
    // probably shouldn't do this is a loop lol
    val aiocb = nativeHeap.alloc<aiocb>()
    aiocb.aio_fildes = fileDescriptor
    aiocb.aio_buf = buffer.reinterpret()
    aiocb.aio_nbytes = bytesToRead.toULong()
    aiocb.aio_offset = offset

    if (aio_read(aiocb.ptr) == -1) {
        return Err(errno.aioReadError())
    }

    while (aio_error(aiocb.ptr) == EINPROGRESS) {
        sched_yield()
    }

    val err = aio_error(aiocb.ptr)
    if (err != 0) {
        return Err(err.aioReadError())
    }

    val bytesRead = aio_return(aiocb.ptr)
    aiocb.aio_offset += bytesRead

    nativeHeap.free(aiocb)

    return Ok(bytesRead)
}
