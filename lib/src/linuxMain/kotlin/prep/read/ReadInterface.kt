package prep.read

import kotlinx.cinterop.CValuesRef
import liburing.io_uring_prep_read
import liburing.io_uring_prep_read_fixed
import liburing.io_uring_prep_readv
import liburing.io_uring_sqe
import liburing.iovec

internal interface PrepReadInterface {
    fun prepRead(
        sqe: CValuesRef<io_uring_sqe>?,
        fileDescriptor: Int,
        buffer: CValuesRef<*>?,
        bytesToRead: UInt,
        offset: ULong
    )
}

internal interface PrepReadFixedInterface {
    fun prepReadFixed(
        sqe: CValuesRef<io_uring_sqe>?,
        fileDescriptor: Int,
        buffer: CValuesRef<*>?,
        bytesToRead: UInt,
        offset: ULong,
        bufIndex: Int
    )
}

internal interface PrepReadVInterface {
    fun prepReadV(
        sqe: CValuesRef<io_uring_sqe>?,
        fileDescriptor: Int,
        iovecs: CValuesRef<iovec>?,
        nrVecs: UInt,
        offset: ULong
    )
}

internal interface ReadInterface : PrepReadInterface, PrepReadFixedInterface, PrepReadVInterface

internal object IOUringReadInterface : ReadInterface {
    override fun prepRead(
        sqe: CValuesRef<io_uring_sqe>?,
        fileDescriptor: Int,
        buffer: CValuesRef<*>?,
        bytesToRead: UInt,
        offset: ULong
    ) = io_uring_prep_read(sqe, fileDescriptor, buffer, bytesToRead, offset)

    override fun prepReadFixed(
        sqe: CValuesRef<io_uring_sqe>?,
        fileDescriptor: Int,
        buffer: CValuesRef<*>?,
        bytesToRead: UInt,
        offset: ULong,
        bufIndex: Int
    ) = io_uring_prep_read_fixed(sqe, fileDescriptor, buffer, bytesToRead, offset, bufIndex)

    override fun prepReadV(
        sqe: CValuesRef<io_uring_sqe>?,
        fileDescriptor: Int,
        iovecs: CValuesRef<iovec>?,
        nrVecs: UInt,
        offset: ULong
    ) = io_uring_prep_readv(sqe, fileDescriptor, iovecs, nrVecs, offset)
}
