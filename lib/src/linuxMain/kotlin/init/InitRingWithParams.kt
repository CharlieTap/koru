package init

import alloc.KoruAllocator
import kotlinx.cinterop.*
import liburing.*
import ring.Ring
import alloc.allocator
import alloc.allocLongLived

value class RingEntries(internal val entries: UInt)
value class SQThreadCpu(internal val cpu:                   UInt)
value class SQThreadIdleInMillis(internal val millis: UInt)
value class WorkQueueFileDescriptor(internal val fd: UInt)

data class RingConfiguration(
    /**
     * Number of submission queue entries requested. The kernel may adjust this value.
     * Default: 0 (kernel will choose an appropriate value)
     */
    val sqEntries: UInt = 0u,

    /**
     * Number of completion queue entries requested. The kernel may adjust this value.
     * Default: 0 (kernel will choose an appropriate value)
     */
    val cqEntries: UInt = 0u,

    /**
     * Flags for setting up the io_uring instance (e.g., IORING_SETUP_SQPOLL, IORING_SETUP_SQ_AFF, etc.).
     * Default: 0 (No flags set)
     */
    val flags: RingFlags = RingFlags.default(),

    /**
     * CPU core for the kernel submission queue polling thread to run on (if IORING_SETUP_SQPOLL is set).
     * Default: 0 (any CPU core)
     */
    val sqThreadCpu: SQThreadCpu = SQThreadCpu(0u),

    /**
     * Milliseconds of idle time before the kernel submission queue polling thread is put to sleep (if IORING_SETUP_SQPOLL is set).
     * Default: 0 (kernel-defined idle time)
     */
    val sqThreadIdle: SQThreadIdleInMillis = SQThreadIdleInMillis(0u),

    /**
     * File descriptor of an existing workqueue if attaching to an existing io_uring instance (IORING_SETUP_ATTACH_WQ).
     * Default: 0 (no workqueue to attach)
     */
    val wqFd: WorkQueueFileDescriptor = WorkQueueFileDescriptor(0u)
)

data class RingDetails(
    /**
     * The number of entries in the submission queue (SQ).
     */
    val sqEntries: UInt,

    /**
     * The number of entries in the completion queue (CQ).
     */
    val cqEntries: UInt,

    /**
     * A set of features supported by the io_uring instance.
     */
    val features: RingFeatures,

    /**
     * File descriptor for the shared asynchronous worker thread backend.
     */
    val wqFd: WorkQueueFileDescriptor,
)

sealed class InitRingResult {

    data class Success(
        val ring: Ring,
        val details: RingDetails,
    ): InitRingResult()

    sealed class Failure: InitRingResult() {
        data object InvalidArgument : Failure()
        data object InsufficientMemory : Failure()
        data object TemporaryResourceShortage : Failure()
        data object InvalidPointer : Failure()
        data object FileDescriptorLimitReached : Failure()
        data class Other(val err: Int): Failure()
    }
}

fun interface InitRing: (RingEntries, RingConfiguration?) -> InitRingResult

fun initRing(
    entries: RingEntries,
    configuration: RingConfiguration? = null,
) = InitRing { _, _ ->
    memScoped {
        initRing(entries, configuration, IOUringInitInterface, allocator(this, nativeHeap))
    }
}(entries, configuration)

internal fun initRing(
    entries: RingEntries,
    configuration: RingConfiguration?,
    initInterface: InitInterface,
    allocator: KoruAllocator,
): InitRingResult {

    val ring = allocator.allocLongLived<io_uring>()
    val params = allocator.alloc<io_uring_params>().apply {
        sq_entries = configuration?.sqEntries ?: 0u
        cq_entries = configuration?.cqEntries ?: 0u
        flags = configuration?.flags?.flags ?: 0u
        wq_fd = configuration?.wqFd?.fd ?: 0u
        sq_thread_cpu = configuration?.sqThreadCpu?.cpu ?: 0u
        sq_thread_idle = configuration?.sqThreadIdle?.millis ?: 0u
    }

    val result = initInterface.initialiseQueueWithParams(entries.entries, ring.ptr, params.ptr)

    if(result != 0) {
        val error = when (result) {
            EINVAL -> InitRingResult.Failure.InvalidArgument
            ENOMEM -> InitRingResult.Failure.InsufficientMemory
            EAGAIN -> InitRingResult.Failure.TemporaryResourceShortage
            EFAULT -> InitRingResult.Failure.InvalidPointer
            EMFILE -> InitRingResult.Failure.FileDescriptorLimitReached
            else -> InitRingResult.Failure.Other(result)
        }
        return error
    }

    val details = RingDetails(
        params.sq_entries,
        params.cq_entries,
        RingFeatures(params.features),
        WorkQueueFileDescriptor(params.wq_fd),
    )

    return InitRingResult.Success(
        Ring(ring.ptr), details
    )
}