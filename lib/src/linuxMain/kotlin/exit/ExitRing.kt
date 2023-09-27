package exit

import ring.Ring

fun interface ExitRing: (Ring) -> Unit

fun exitRing(ring: Ring) = ExitRing {
    exitRing(ring, IOUringExitInterface)
}(ring)

internal fun exitRing(
    ring: Ring,
    exitInterface: ExitInterface,
) {
    exitInterface.cleanupRing(ring.ring)
}
