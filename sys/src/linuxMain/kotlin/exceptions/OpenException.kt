package exceptions

import call.OpenError

data class OpenException(
    private val filePath: String,
    private val openError: OpenError,
): Exception(
    "Failed to open file $filePath, $openError"
)
