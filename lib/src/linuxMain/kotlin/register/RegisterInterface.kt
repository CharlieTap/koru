package register

import kotlinx.cinterop.CArrayPointer
import kotlinx.cinterop.CValuesRef
import kotlinx.cinterop.IntVar
import liburing.io_uring
import liburing.io_uring_register_files

internal interface RegisterFilesInterface {
    fun registerFiles(ring: CValuesRef<io_uring>?, files: CArrayPointer<IntVar>?, count: UInt): Int
}

internal interface RegisterInterface : RegisterFilesInterface

internal object IOUringRegisterInterface : RegisterInterface {
    override fun registerFiles(ring: CValuesRef<io_uring>?, files: CArrayPointer<IntVar>?, count: UInt): Int {
        return io_uring_register_files(ring, files, count)
    }
}
