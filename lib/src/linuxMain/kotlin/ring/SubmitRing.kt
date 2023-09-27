package ring

fun interface SubmitRing: (Ring) -> SubmitRingResult

fun submitRing(ring: Ring): SubmitRingResult = SubmitRing {
    submitRing(it, IOUringRingInterface)
}(ring)

internal fun submitRing(
    ring: Ring,
    ringInterface: SubmitRingInterface,
): SubmitRingResult {
    val result = ringInterface.submit(ring.ring)
    return SubmitRingResult.fromReturnCode(result)
}
