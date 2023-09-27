package exit

import kotlinx.cinterop.CValuesRef
import liburing.io_uring
import liburing.io_uring_queue_exit
import ring.Ring

internal interface ExitInterface {
    fun cleanupRing(ring:  CValuesRef<io_uring>)
}

internal object IOUringExitInterface: ExitInterface {
    override fun cleanupRing(ring: CValuesRef<io_uring>) = io_uring_queue_exit(ring)
}
