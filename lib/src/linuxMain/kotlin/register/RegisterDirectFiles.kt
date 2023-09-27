package register

import kotlinx.cinterop.*
import ring.Ring

sealed class RegisterDirectFilesResult {
    data object Success : RegisterDirectFilesResult()
    data class Failure(val errno: Int) : RegisterDirectFilesResult()
}

fun interface RegisterDirectFiles : (CArrayPointer<IntVar>?, UInt) -> RegisterDirectFilesResult

fun registerDirectFiles(
    ring: Ring,
    numberOfDirectFiles: Int,
): RegisterDirectFilesResult {
    return registerDirectFiles(
        ring,
        numberOfDirectFiles,
        IOUringRegisterInterface,
    )
}

internal fun registerDirectFiles(
    ring: Ring,
    numberOfDirectFiles: Int,
    registerInterface: RegisterFilesInterface,
): RegisterDirectFilesResult {
    // todo we should provide a way to free this on ring tear down
    val files = nativeHeap.allocArray<IntVar>(numberOfDirectFiles) {_ ->
        this.value = -1
    }

    val result = registerInterface.registerFiles(ring.ring, files, numberOfDirectFiles.toUInt())
    return if (result < 0) {
        RegisterDirectFilesResult.Failure(result)
    } else {
        RegisterDirectFilesResult.Success
    }
}
