package init

/**
 * RingFlags are represented as a bit mask of 0 or more of the RingFlags.Builder constants ORed together
 */
value class RingFlags internal constructor(internal val flags: UInt) {

    fun isEnabledIOPoll() = (flags and IORING_SETUP_IOPOLL.toUInt()) != 0u
    fun isEnabledSQPoll() = (flags and IORING_SETUP_SQPOLL.toUInt()) != 0u
    fun isEnabledSQAff() = (flags and IORING_SETUP_SQ_AFF.toUInt()) != 0u
    fun isEnabledCQSize() = (flags and IORING_SETUP_CQSIZE.toUInt()) != 0u
    fun isEnabledClamp() = (flags and IORING_SETUP_CLAMP.toUInt()) != 0u
    fun isEnabledAttachWQ() = (flags and IORING_SETUP_ATTACH_WQ.toUInt()) != 0u
    fun isEnabledRDisabled() = (flags and IORING_SETUP_R_DISABLED.toUInt()) != 0u
    fun isEnabledSubmitAll() = (flags and IORING_SETUP_SUBMIT_ALL.toUInt()) != 0u
    fun isEnabledCoopTaskRun() = (flags and IORING_SETUP_COOP_TASKRUN.toUInt()) != 0u
    fun isEnabledTaskRunFlag() = (flags and IORING_SETUP_TASKRUN_FLAG.toUInt()) != 0u
    fun isEnabledSQE128() = (flags and IORING_SETUP_SQE128.toUInt()) != 0u
    fun isEnabledCQE32() = (flags and IORING_SETUP_CQE32.toUInt()) != 0u
    fun isEnabledSingleIssuer() = (flags and IORING_SETUP_SINGLE_ISSUER.toUInt()) != 0u
    fun isEnabledDeferTaskRun() = (flags and IORING_SETUP_DEFER_TASKRUN.toUInt()) != 0u

    class Builder {

        private var flags: Int = 0

        /**
         * Perform busy-waiting for an I/O completion, as opposed to getting notifications via an asynchronous IRQ (Interrupt Request).
         *
         * The file system (if any) and block device must support polling in order for this to work. Busy-waiting provides
         * lower latency, but may consume more CPU resources than interrupt driven I/O.
         *
         * Currently, this feature is usable only on a file descriptor opened using the O_DIRECT flag. When a read or write is
         * submitted to a polled context, the application must poll for completions on the CQ ring by calling io_uring_enter(2).
         * It is illegal to mix and match polled and non-polled I/O on an io_uring instance.
         *
         * This is only applicable for storage devices for now, and the storage device must be configured for polling. How to do
         * that depends on the device type in question. For NVMe devices, the nvme driver must be loaded with the poll_queues
         * parameter set to the desired number of polling queues. The polling queues will be shared appropriately between the CPUs
         * in the system, if the number is less than the number of online CPU threads.
         */
        fun enableIOPoll(): Builder {
            flags = flags or IORING_SETUP_IOPOLL
            return this
        }

        /**
         * Kernel thread created to perform submission queue polling.
         * Enables issuing I/O without context switching into the kernel. Requires proper kernel version and permissions.
         * 
         * Use a kernel thread to perform submission queue polling. This allows your application to
         * issue I/O without ever context switching into the kernel, however it does use up a lot more
         * CPU. You should use it when you are expecting very large amounts of I/O.
         *
         * After `idle` milliseconds, the kernel thread will go to sleep and you will have to wake it up
         * again with a system call (this is handled by [`Submitter::submit`] and
         * [`Submitter::submit_and_wait`] automatically).
         *
         * Before version 5.11 of the Linux kernel, to successfully use this feature, the application
         * must register a set of files to be used for IO through io_uring_register(2) using the
         * IORING_REGISTER_FILES opcode. Failure to do so will result in submitted IO being errored
         * with EBADF. The presence of this feature can be detected by the IORING_FEAT_SQPOLL_NONFIXED
         * feature flag. In version 5.11 and later, it is no longer necessary to register files to use
         * this feature. 5.11 also allows using this as non-root, if the user has the CAP_SYS_NICE
         * capability. In 5.13 this requirement was also relaxed, and no special privileges are needed
         * for SQPOLL in newer kernels. Certain stable kernels older than 5.13 may also support
         * unprivileged SQPOLL.
         */
        fun enableSQPoll(): Builder {
            flags = flags or IORING_SETUP_SQPOLL
            return this
        }

        /**
         * Bind the poll thread to the CPU set in sq_thread_cpu.
         * Only meaningful when IORING_SETUP_SQPOLL [enableSQPoll] is specified.
         *
         * When cgroup setting cpuset.cpus changes (typically in container environment), the bounded cpu
         * set may be changed as well.
         */
        fun enableSQAff(): Builder {
            flags = flags or IORING_SETUP_SQ_AFF
            return this
        }

        /**
         * Create the completion queue with specified entries.
         * The value must be greater than the size than the value provided in the ring, and may be rounded up to the next power-of-two.
         */
        fun enableCQSize(): Builder {
            flags = flags or IORING_SETUP_CQSIZE
            return this
        }

        /**
         * If this flag is activated, the following behavior is enforced:
         * - If the `entries` value exceeds `IORING_MAX_ENTRIES`, it will be clamped to `IORING_MAX_ENTRIES`.
         * - If the `IORING_SETUP_SQPOLL` flag is set and the value of `struct io_uring_params.cq_entries`
         *   exceeds `IORING_MAX_CQ_ENTRIES`, then it will be clamped to `IORING_MAX_CQ_ENTRIES`.
         *
         * This ensures that the specified entries and completion queue entries are within acceptable limits,
         * providing a safeguard against incorrect or excessive values.
         */
        fun enableClamp(): Builder {
            flags = flags or IORING_SETUP_CLAMP
            return this
        }

        /**
         * This flag is intended to be used in conjunction with `struct io_uring_params.wq_fd` being set to an existing io_uring ring file descriptor.
         * When activated, the io_uring instance being created will share the asynchronous worker thread backend of the specified io_uring ring.
         * As a result, a new separate thread pool is not created, and instead, the existing infrastructure is leveraged for more efficient resource utilization.
         */
        fun enableAttachWQ(): Builder {
            flags = flags or IORING_SETUP_ATTACH_WQ
            return this
        }

        /**
         * If this flag is activated, the io_uring ring begins in a disabled state. During this state, restrictions can be registered,
         * but submissions of I/O operations are prohibited. Refer to the io_uring_register(2) documentation for instructions on how
         * to transition the ring to an enabled state.
         * Available since 5.10.
         */
        fun enableRDisabled(): Builder {
            flags = flags or IORING_SETUP_R_DISABLED
            return this
        }

        /**
         * Normally, io_uring halts the submission of a batch of requests if one of them results in an error.
         * This can lead to the submission of fewer requests than expected if a request ends in error during submission.
         * If the ring is created with this flag, io_uring_enter(2) will continue submitting requests even if it encounters an error with a request.
         * CQEs are still posted for errored requests regardless of whether or not this flag is set at ring creation time;
         * the only difference is whether the submit sequence is halted or continued when an error is observed.
         * Available since kernel version 5.18.
         */
        fun enableSubmitAll(): Builder {
            flags = flags or IORING_SETUP_SUBMIT_ALL
            return this
        }

        /**
         * Reduce performance impact of forceful interruption for completions.
         * Suitable for most use cases except multi-threaded operations on the same ring.
         * 
         * By default, io_uring will interrupt a task running in userspace when a completion event
         * comes in. This is to ensure that completions run in a timely manner. For a lot of use
         * cases, this is overkill and can cause reduced performance from both the inter-processor
         * interrupt used to do this, the kernel/user transition, the needless interruption of the
         * tasks userspace activities, and reduced batching if completions come in at a rapid rate.
         * Most applications don't need the forceful interruption, as the events are processed at any
         * kernel/user transition. The exception are setups where the application uses multiple
         * threads operating on the same ring, where the application waiting on completions isn't the
         * one that submitted them. For most other use cases, setting this flag will improve
         * performance. Available since 5.19.
         */
        fun enableCoopTaskRun(): Builder {
            flags = flags or IORING_SETUP_COOP_TASKRUN
            return this
        }

        /**
         * Provides a flag for pending completions, safe to use with peek operations.
         * 
         * Used in conjunction with IORING_SETUP_COOP_TASKRUN, this provides a flag,
         * IORING_SQ_TASKRUN, which is set in the SQ ring flags whenever completions are pending that
         * should be processed. As an example, liburing will check for this flag even when doing
         * io_uring_peek_cqe(3) and enter the kernel to process them, and applications can do the
         * same. This makes IORING_SETUP_TASKRUN_FLAG safe to use even when applications rely on a
         * peek style operation on the CQ ring to see if anything might be pending to reap.
         * Available since 5.19.
         */
        fun enableTaskRunFlag(): Builder {
            flags = flags or IORING_SETUP_TASKRUN_FLAG
            return this
        }

        /**
         * If set, io_uring will use 128-byte SQEs rather than the normal 64-byte sized variant.
         * This is a requirement for using certain request types. As of kernel version 5.19,
         * only the IORING_OP_URING_CMD passthrough command for NVMe passthrough needs this.
         * Available since kernel version 5.19.
         */
        fun enableSQE128(): Builder {
            flags = flags or IORING_SETUP_SQE128
            return this
        }

        /**
         * If set, io_uring will use 32-byte CQEs rather than the normal 16-byte sized variant.
         * This is a requirement for using certain request types. As of kernel version 5.19,
         * only the IORING_OP_URING_CMD passthrough command for NVMe passthrough needs this.
         * Available since kernel version 5.19.
         */
        fun enableCQE32(): Builder {
            flags = flags or IORING_SETUP_CQE32
            return this
        }

        /**
         * Hint for single task submission. Enforced by the kernel, suitable with IORING_SETUP_SQPOLL.
         * 
         * Hint the kernel that a single task will submit requests. Used for optimizations. This is
         * enforced by the kernel, and request that don't respect that will fail with -EEXIST.
         * If [enableSQPoll] is enabled, the polling task is doing the submissions and multiple
         * userspace tasks can call io_uring_enter() and higher level APIs.
         * Available since 6.0.
         */
        fun enableSingleIssuer(): Builder {
            flags = flags or IORING_SETUP_SINGLE_ISSUER
            return this
        }

        /**
         * Defer work until an io_uring_enter call with IORING_ENTER_GETEVENTS.
         * Requires IORING_SETUP_SINGLE_ISSUER and specific thread handling.
         * 
         * By default, io_uring will process all outstanding work at the end of any system call or
         * thread interrupt. This can delay the application from making other progress. Setting this
         * flag will hint to io_uring that it should defer work until an io_uring_enter(2) call with
         * the IORING_ENTER_GETEVENTS flag set. This allows the application to request work to run
         * just just before it wants to process completions. This flag requires the
         * IORING_SETUP_SINGLE_ISSUER flag to be set, and also enforces that the call to
         * io_uring_enter(2) is called from the same thread that submitted requests. Note that if this
         * flag is set then it is the application's responsibility to periodically trigger work (for
         * example via any of the CQE waiting functions) or else completions may not be delivered.
         * Available since 6.1.
         */
        fun enableDeferTaskRun(): Builder {
            flags = flags or IORING_SETUP_DEFER_TASKRUN
            return this
        }

        /**
         * Kotlin doesn't support const expressions on UInt like 1u shl 1
         * thus we convert at runtime 😒
         */
        fun build() = RingFlags(flags.toUInt())
    }

    override fun toString(): String {
        return buildString {
            append("Ring Flags:\n")
            append("Flag                          | Enabled\n")
            append("---------------------------------------\n")
            append("I/O Poll                      | ${isEnabledIOPoll()}\n")
            append("SQ Poll                       | ${isEnabledSQPoll()}\n")
            append("SQ Affinity                   | ${isEnabledSQAff()}\n")
            append("CQ Size                       | ${isEnabledCQSize()}\n")
            append("Clamp                         | ${isEnabledClamp()}\n")
            append("Attach Work Queue             | ${isEnabledAttachWQ()}\n")
            append("Ring Disabled                 | ${isEnabledRDisabled()}\n")
            append("Submit All                    | ${isEnabledSubmitAll()}\n")
            append("Cooperative Task Run          | ${isEnabledCoopTaskRun()}\n")
            append("Task Run Flag                 | ${isEnabledTaskRunFlag()}\n")
            append("SQE 128                       | ${isEnabledSQE128()}\n")
            append("CQE 32                        | ${isEnabledCQE32()}\n")
            append("Single Issuer                 | ${isEnabledSingleIssuer()}\n")
            append("Defer Task Run                | ${isEnabledDeferTaskRun()}\n")
        }
    }

    companion object {
        internal const val IORING_SETUP_IOPOLL = 1 shl 0
        internal const val IORING_SETUP_SQPOLL = 1 shl 1
        internal const val IORING_SETUP_SQ_AFF = 1 shl 2
        internal const val IORING_SETUP_CQSIZE = 1 shl 3
        internal const val IORING_SETUP_CLAMP = 1 shl 4
        internal const val IORING_SETUP_ATTACH_WQ = 1 shl 5
        internal const val IORING_SETUP_R_DISABLED = 1 shl 6
        internal const val IORING_SETUP_SUBMIT_ALL = 1 shl 7
        internal const val IORING_SETUP_COOP_TASKRUN = 1 shl 8
        internal const val IORING_SETUP_TASKRUN_FLAG = 1 shl 9
        internal const val IORING_SETUP_SQE128 = 1 shl 10
        internal const val IORING_SETUP_CQE32 = 1 shl 11
        internal const val IORING_SETUP_SINGLE_ISSUER = 1 shl 12
        internal const val IORING_SETUP_DEFER_TASKRUN = 1 shl 13

        /**
         * By default, the io_uring instance is setup for interrupt driven I/O.
         * I/O may be submitted using io_uring_enter(2) and can be reaped by polling the completion queue.
         */
        fun default(): RingFlags = Builder().build()
    }
}

fun ringFlags(block: RingFlags.Builder.() -> Unit): RingFlags = RingFlags.Builder().apply(block).build()
