package queue.cqe

import ring.Ring
import kotlinx.cinterop.*
import liburing.io_uring_cqe

fun interface PeekCompletionQueueEntry: (Ring) -> CompletionQueueEntry?

fun peekCompletionQueueEntry(ring: Ring) = PeekCompletionQueueEntry {
    memScoped {
        peekCompletionQueueEntry(it, IOUringCQEInterface, this)
    }
}(ring)

internal fun peekCompletionQueueEntry(
    ring: Ring,
    cqeInterface: PeekCQEInterface,
    allocator: NativePlacement,
): CompletionQueueEntry? {
    val cqe = allocator.alloc<CPointerVar<io_uring_cqe>>()
    val result = cqeInterface.peekCompletionQueueEntry(ring.ring, cqe.ptr)
    return if (result == 0) {
        CompletionQueueEntry(cqe)
    } else {
        null
    }
}
