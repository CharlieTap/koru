package queue.cqe

import kotlinx.cinterop.CPointerVar
import kotlinx.cinterop.pointed
import liburing.io_uring_cqe

data class CompletionQueueEntry(
    val flags: UInt,
    val result: Int,
    val userData: ULong
) {
    internal lateinit var ptr: CPointerVar<io_uring_cqe>

    internal constructor(cqe: CPointerVar<io_uring_cqe>) : this(
        flags = cqe.pointed?.flags ?: 0u,
        result = cqe.pointed?.res ?: 0,
        userData = cqe.pointed?.user_data ?: 0uL
    ) {
        ptr = cqe
    }
}