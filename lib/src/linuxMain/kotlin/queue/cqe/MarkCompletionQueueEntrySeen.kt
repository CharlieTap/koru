package queue.cqe

import ring.Ring
import kotlinx.cinterop.*

fun interface MarkCompletionQueueEntrySeen: (Ring, CompletionQueueEntry) -> Unit

fun markCompletionQueueEntrySeen(ring: Ring, cqe: CompletionQueueEntry) = MarkCompletionQueueEntrySeen { _, _ ->
    markCompletionQueueEntrySeen(ring, cqe, IOUringCQEInterface)
}(ring, cqe)

internal fun markCompletionQueueEntrySeen(
    ring: Ring,
    cqe: CompletionQueueEntry,
    cqeInterface: SeenCQEInterface,
) {
    cqeInterface.markCompletionQueueEntrySeen(ring.ring, cqe.ptr.value)
}
