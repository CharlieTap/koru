package prep.open

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CValuesRef
import liburing.*

internal interface PrepOpenAtInterface {
    fun prepOpenAt(
        sqe: CValuesRef<io_uring_sqe>?,
        dirFd: Int,
        path: CValuesRef<ByteVar>?,
        flags: Int,
        mode: UInt
    )
}

internal interface PrepOpenAt2Interface {
    fun prepOpenAt2(
        sqe: CValuesRef<io_uring_sqe>?,
        dirFd: Int,
        path: CValuesRef<ByteVar>?,
        how: CValuesRef<open_how>?
    )
}

internal interface PrepOpenAt2DirectInterface {
    fun prepOpenAt2Direct(
        sqe: CValuesRef<io_uring_sqe>?,
        dirFd: Int,
        path: CValuesRef<ByteVar>?,
        how: CValuesRef<open_how>?,
        fileIndex: UInt
    )
}

internal interface PrepOpenAtDirectInterface {
    fun prepOpenAtDirect(
        sqe: CValuesRef<io_uring_sqe>?,
        dirFd: Int,
        path: CValuesRef<ByteVar>?,
        flags: Int,
        mode: UInt,
        fileIndex: UInt
    )
}

internal interface OpenInterface :
    PrepOpenAtInterface,
    PrepOpenAt2Interface,
    PrepOpenAt2DirectInterface,
    PrepOpenAtDirectInterface

internal object IOUringOpenInterface : OpenInterface {
    override fun prepOpenAt(
        sqe: CValuesRef<io_uring_sqe>?,
        dirFd: Int,
        path: CValuesRef<ByteVar>?,
        flags: Int,
        mode: UInt
    ) = io_uring_prep_openat(sqe, dirFd, path, flags, mode)

    override fun prepOpenAt2(
        sqe: CValuesRef<io_uring_sqe>?,
        dirFd: Int,
        path: CValuesRef<ByteVar>?,
        how: CValuesRef<open_how>?
    ) = io_uring_prep_openat2(sqe, dirFd, path, how)

    override fun prepOpenAt2Direct(
        sqe: CValuesRef<io_uring_sqe>?,
        dirFd: Int,
        path: CValuesRef<ByteVar>?,
        how: CValuesRef<open_how>?,
        fileIndex: UInt
    ) = io_uring_prep_openat2_direct(sqe, dirFd, path, how, fileIndex)

    override fun prepOpenAtDirect(
        sqe: CValuesRef<io_uring_sqe>?,
        dirFd: Int,
        path: CValuesRef<ByteVar>?,
        flags: Int,
        mode: UInt,
        fileIndex: UInt
    ) = io_uring_prep_openat_direct(sqe, dirFd, path, flags, mode, fileIndex)
}
