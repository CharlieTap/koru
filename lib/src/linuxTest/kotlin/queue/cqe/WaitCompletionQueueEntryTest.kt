package queue.cqe

import alloc.fakeAllocator
import alloc.testAllocator
import kotlinx.cinterop.*
import liburing.io_uring
import liburing.io_uring_cqe
import ring.Ring
import kotlin.test.Test
import kotlin.test.assertEquals

internal class WaitCompletionQueueEntryTest {

    @Test
    fun `wait for a completion queue entry successfully`() = memScoped {

        val fakeInterface = object: WaitCQEInterface {
            override fun waitCompletionQueueEntry(
                ring: CValuesRef<io_uring>,
                cqe: CValuesRef<CPointerVar<io_uring_cqe>>
            ): Int = 0
        }

        val cqe = alloc<CPointerVar<io_uring_cqe>>()
        val fakeAllocator = fakeAllocator(stackAllocations = listOf(cqe).iterator())

        val ring = Ring(alloc<io_uring>().ptr)
        val result = waitCompletionQueueEntry(ring, fakeInterface, fakeAllocator)

        assertEquals(CompletionQueueEntry(cqe), result)
    }

    @Test
    fun `failure to wait for cqe returns null`() = memScoped {

        val fakeInterface = object: WaitCQEInterface {
            override fun waitCompletionQueueEntry(
                ring: CValuesRef<io_uring>,
                cqe: CValuesRef<CPointerVar<io_uring_cqe>>
            ): Int = -1
        }

        val ring = Ring(alloc<io_uring>().ptr)
        val result = waitCompletionQueueEntry(ring, fakeInterface, testAllocator())

        assertEquals(null, result)
    }
}
