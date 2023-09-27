package ffi

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import platform.posix.*
import lib.getBlockSize

sealed class BlockSizeError {
    data object PathNotFound : BlockSizeError()
    data class Other(val error: Int) : BlockSizeError()
}

private fun Int.blockSizeError() = when (this) {
    ENOENT -> BlockSizeError.PathNotFound
    else -> BlockSizeError.Other(this)
}

fun getDeviceBlockSize(path: String): Result<ULong, BlockSizeError> {
    val blockSize = getBlockSize(path)

    return if (blockSize == 0UL) {
        Err(errno.blockSizeError())
    } else {
        Ok(blockSize)
    }
}
