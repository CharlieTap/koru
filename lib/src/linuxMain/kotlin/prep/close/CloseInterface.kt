package prep.close

import kotlinx.cinterop.CValuesRef
import liburing.io_uring_prep_close
import liburing.io_uring_prep_close_direct
//import liburing.io_uring_prep_close_direct
import liburing.io_uring_sqe

internal interface PrepCloseInterface {
    fun prepClose(sqe: CValuesRef<io_uring_sqe>, fileDescriptor: Int)
}

internal interface PrepCloseDirectInterface {
    fun prepCloseDirect(sqe: CValuesRef<io_uring_sqe>, fileIndex: UInt)
}

internal interface CloseInterface : PrepCloseInterface, PrepCloseDirectInterface

internal object IOUringCloseInterface : CloseInterface {
    override fun prepClose(sqe: CValuesRef<io_uring_sqe>, fileDescriptor: Int) =
        io_uring_prep_close(sqe, fileDescriptor)

    override fun prepCloseDirect(sqe: CValuesRef<io_uring_sqe>, fileIndex:UInt) =
        io_uring_prep_close_direct(sqe, fileIndex)
}
