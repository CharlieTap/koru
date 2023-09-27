package init

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CValuesRef
import liburing.io_uring
import liburing.io_uring_params
import liburing.io_uring_queue_init
import liburing.io_uring_queue_init_params

internal interface InitInterface {
    /*
        Unused for now as it doesn't support returning ring information through params
     */
    fun initialiseQueue(
        queueSize: UInt,
        ring: CValuesRef<io_uring>,
        flags: UInt,
        ): Int

    fun initialiseQueueWithParams(
        queueSize: UInt,
        ring: CValuesRef<io_uring>,
        params: CPointer<io_uring_params>,
    ): Int
}

internal object IOUringInitInterface: InitInterface {

    override fun initialiseQueue(queueSize: UInt, ring: CValuesRef<io_uring>, flags: UInt): Int =
        io_uring_queue_init(queueSize, ring, flags)

    override fun initialiseQueueWithParams(
        queueSize: UInt,
        ring: CValuesRef<io_uring>,
        params: CPointer<io_uring_params>
    ): Int = io_uring_queue_init_params(queueSize, ring, params)
}
