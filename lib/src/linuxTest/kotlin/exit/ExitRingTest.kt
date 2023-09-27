package exit

import kotlinx.cinterop.*
import liburing.io_uring
import ring.Ring
import kotlin.test.Test
import kotlin.test.assertEquals

internal class ExitRingTest {

    @Test
    fun `exit ring calls through to liburing's exit ring successfully`() = memScoped {
        val expectedRing = alloc<io_uring>().ptr
        val ring = Ring(expectedRing)
        val exitInterface = object : ExitInterface {
            override fun cleanupRing(ring:  CValuesRef<io_uring>) {
                assertEquals(expectedRing, ring)
            }
        }

        val result = exitRing(ring, exitInterface)

        assertEquals(Unit, result)
    }
}
