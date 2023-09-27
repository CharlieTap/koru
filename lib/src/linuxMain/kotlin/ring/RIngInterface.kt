package ring

import kotlinx.cinterop.CValuesRef
import liburing.io_uring
import liburing.io_uring_submit
import liburing.io_uring_submit_and_wait

internal interface SubmitRingInterface {
    fun submit(ring: CValuesRef<io_uring>): Int
}

internal interface SubmitRingAndWaitInterface {
    fun submitAndWait(
        ring: CValuesRef<io_uring>,
        completionEventsToWaitFor: UInt,
    ): Int
}

internal interface RingInterface: SubmitRingInterface, SubmitRingAndWaitInterface

internal object IOUringRingInterface : RingInterface {
    override fun submit(ring: CValuesRef<io_uring>): Int {
        return io_uring_submit(ring)
    }

    override fun submitAndWait(ring: CValuesRef<io_uring>, completionEventsToWaitFor: UInt): Int =
        io_uring_submit_and_wait(ring, completionEventsToWaitFor)
}
