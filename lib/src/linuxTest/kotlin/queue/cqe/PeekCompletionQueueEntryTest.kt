package queue.cqe

import alloc.fakeAllocator
import alloc.testAllocator
import kotlinx.cinterop.*
import liburing.io_uring
import liburing.io_uring_cqe
import ring.Ring
import kotlin.test.Test
import kotlin.test.assertEquals

internal class PeekCompletionQueueEntryTest {

    @Test
    fun `peek a completion queue entry successfully`() = memScoped {

        val fakeInterface = object: PeekCQEInterface {
            override fun peekCompletionQueueEntry(
                ring: CValuesRef<io_uring>,
                cqe: CValuesRef<CPointerVar<io_uring_cqe>>
            ): Int = 0
        }

        val cqe = alloc<CPointerVar<io_uring_cqe>>()
        val fakeAllocator = fakeAllocator(stackAllocations = listOf(cqe).iterator())

        val ring = Ring(alloc<io_uring>().ptr)
        val result = peekCompletionQueueEntry(ring, fakeInterface, fakeAllocator)

        assertEquals(CompletionQueueEntry(cqe), result)
    }

    @Test
    fun `failure to peek cqe returns null`() = memScoped {

        val fakeInterface = object: PeekCQEInterface {
            override fun peekCompletionQueueEntry(
                ring: CValuesRef<io_uring>,
                cqe: CValuesRef<CPointerVar<io_uring_cqe>>
            ): Int = -1
        }

        val ring = Ring(alloc<io_uring>().ptr)
        val result = peekCompletionQueueEntry(ring, fakeInterface, testAllocator())

        assertEquals(null, result)
    }
}
