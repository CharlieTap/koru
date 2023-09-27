package queue.cqe

import ring.Ring
import kotlinx.cinterop.*
import liburing.io_uring_cqe

fun interface WaitCompletionQueueEntry: (Ring) -> CompletionQueueEntry?

fun waitCompletionQueueEntry(ring: Ring) = WaitCompletionQueueEntry {
    memScoped {
        waitCompletionQueueEntry(it, IOUringCQEInterface, this)
    }
}(ring)

internal fun waitCompletionQueueEntry(
    ring: Ring,
    cqeInterface: WaitCQEInterface,
    allocator: NativePlacement,
): CompletionQueueEntry? {

    val cqe = allocator.alloc<CPointerVar<io_uring_cqe>>()
    val result = cqeInterface.waitCompletionQueueEntry(ring.ring, cqe.ptr)

    return if (result == 0) {
        CompletionQueueEntry(cqe)
    } else {
        null
    }
}
