package queue.cqe

import kotlinx.cinterop.CPointerVar
import kotlinx.cinterop.CValuesRef
import liburing.io_uring
import liburing.io_uring_cqe
import liburing.io_uring_cqe_seen
import liburing.io_uring_peek_cqe
import liburing.io_uring_wait_cqe

internal interface PeekCQEInterface {
    fun peekCompletionQueueEntry(
        ring: CValuesRef<io_uring>,
        cqe: CValuesRef<CPointerVar<io_uring_cqe>>
    ): Int
}

internal interface WaitCQEInterface {
    fun waitCompletionQueueEntry(
        ring: CValuesRef<io_uring>,
        cqe: CValuesRef<CPointerVar<io_uring_cqe>>
    ): Int
}

internal interface SeenCQEInterface {
    fun markCompletionQueueEntrySeen(
        ring: CValuesRef<io_uring>,
        cqe: CValuesRef<io_uring_cqe>?
    )
}

internal interface CQEInterface: PeekCQEInterface, WaitCQEInterface, SeenCQEInterface

internal object IOUringCQEInterface: CQEInterface {
    override fun peekCompletionQueueEntry(
        ring: CValuesRef<io_uring>,
        cqe: CValuesRef<CPointerVar<io_uring_cqe>>
    ): Int = io_uring_peek_cqe(ring, cqe)

    override fun waitCompletionQueueEntry(
        ring: CValuesRef<io_uring>,
        cqe: CValuesRef<CPointerVar<io_uring_cqe>>
    ): Int = io_uring_wait_cqe(ring, cqe)

    override fun markCompletionQueueEntrySeen(
        ring: CValuesRef<io_uring>,
        cqe: CValuesRef<io_uring_cqe>?
    ) = io_uring_cqe_seen(ring, cqe)
}
