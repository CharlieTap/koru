package ring

fun interface SubmitRingAndWait: (Ring, Int) -> SubmitRingResult

fun submitRingAndWait(ring: Ring, completionEventsToWaitFor: Int): SubmitRingResult = SubmitRingAndWait { _, _ ->
    submitRingAndWait(ring, completionEventsToWaitFor, IOUringRingInterface)
}(ring, completionEventsToWaitFor)

internal fun submitRingAndWait(
    ring: Ring,
    completionEventsToWaitFor: Int,
    ringInterface: SubmitRingAndWaitInterface,
): SubmitRingResult {
    val result = ringInterface.submitAndWait(ring.ring, completionEventsToWaitFor.toUInt())
    return SubmitRingResult.fromReturnCode(result)
}
