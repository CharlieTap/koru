package exceptions

import call.ReadError

data class ReadException(
    private val filePath: String,
    private val readError: ReadError,
): Exception(
    "Failed to read from file $filePath, $readError"
)
