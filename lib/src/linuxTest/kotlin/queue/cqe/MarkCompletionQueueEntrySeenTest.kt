package queue.cqe

import kotlinx.cinterop.*
import kotlin.test.Test
import kotlin.test.assertEquals
import liburing.io_uring
import liburing.io_uring_cqe
import ring.Ring

internal class MarkCompletionQueueEntrySeenTest {

    @Test
    fun `calls through to markCompletionQueueEntrySeen successfully`() = memScoped {
        val expectedRing = Ring(alloc<io_uring>().ptr)
        val expectedCQE = CompletionQueueEntry(alloc<CPointerVar<io_uring_cqe>>())

        val seenInterface = object : SeenCQEInterface {
            override fun markCompletionQueueEntrySeen(
                ring: CValuesRef<io_uring>,
                cqe: CValuesRef<io_uring_cqe>?
            ) {
                assertEquals(expectedRing.ring, ring)
                assertEquals(expectedCQE.ptr.value, cqe)
            }
        }

        markCompletionQueueEntrySeen(expectedRing, expectedCQE, seenInterface)
    }
}
