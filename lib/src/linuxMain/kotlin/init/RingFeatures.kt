package init

value class RingFeatures internal constructor(val features: UInt) {

    /**
     * Checks if the two SQ and CQ rings can be mapped with a single mmap(2) call.
     * The SQEs must still be allocated separately. This brings the necessary mmap(2) calls down from three to two.
     * Available since kernel 5.4.
     */
    fun isEnabledSingleMMap() = (features and IORING_FEAT_SINGLE_MMAP.toUInt()) != 0u

    /**
     * Checks if io_uring supports almost never dropping completion events.
     * If a completion event occurs and the CQ ring is full, the kernel stores the event internally until such a time that the CQ ring has room for more entries.
     * Available since kernel 5.5.
     */
    fun isEnabledNoDrop() = (features and IORING_FEAT_NODROP.toUInt()) != 0u

    /**
     * Checks if applications can be certain that any data for async offload has been consumed when the kernel has consumed the SQE.
     * Available since kernel 5.5.
     */
    fun isEnabledSubmitStable() = (features and IORING_FEAT_SUBMIT_STABLE.toUInt()) != 0u

    /**
     * Checks if applications can specify offset == -1 with certain operations to mean current file position.
     * Available since kernel 5.6.
     */
    fun isEnabledRWCurPos() = (features and IORING_FEAT_RW_CUR_POS.toUInt()) != 0u

    /**
     * Checks if io_uring guarantees that both sync and async execution of a request assumes the credentials of the task that called io_uring_enter(2).
     * Available since kernel 5.6.
     */
    fun isEnabledCurPersonality() = (features and IORING_FEAT_CUR_PERSONALITY.toUInt()) != 0u

    /**
     * Checks if io_uring supports using an internal poll mechanism to drive data/space readiness.
     * Available since kernel 5.7.
     */
    fun isEnabledFastPoll() = (features and IORING_FEAT_FAST_POLL.toUInt()) != 0u

    /**
     * Checks if the IORING_OP_POLL_ADD command accepts the full 32-bit range of epoll based flags.
     * Available since kernel 5.9.
     */
    fun isEnabledPoll32Bits() = (features and IORING_FEAT_POLL_32BITS.toUInt()) != 0u

    /**
     * Checks if the IORING_SETUP_SQPOLL feature no longer requires the use of fixed files.
     * Available since kernel 5.11.
     */
    fun isEnabledSQPollNonFixed() = (features and IORING_FEAT_SQPOLL_NONFIXED.toUInt()) != 0u

    /**
     * Checks if the io_uring_enter(2) system call supports passing in an extended argument.
     * Available since kernel 5.11.
     */
    fun isEnabledEnterExtArg() = (features and IORING_FEAT_ENTER_EXT_ARG.toUInt()) != 0u

    /**
     * Checks if io_uring is using native workers for its async helpers.
     * Available since kernel 5.12.
     */
    fun isEnabledNativeWorkers() = (features and IORING_FEAT_NATIVE_WORKERS.toUInt()) != 0u

    /**
     * Checks if io_uring supports a variety of features related to fixed files and buffers.
     * Available since kernel 5.13.
     */
    fun isEnabledRSRCTags() = (features and IORING_FEAT_RSRC_TAGS.toUInt()) != 0u

    /**
     * Checks if io_uring supports setting IOSQE_CQE_SKIP_SUCCESS in the submitted SQE.
     * Available since kernel 5.17.
     */
    fun isEnabledCQESkip() = (features and IORING_FEAT_CQE_SKIP.toUInt()) != 0u

    /**
     * Checks if io_uring supports sane assignment of files for SQEs that have dependencies.
     * Available since kernel 5.17.
     */
    fun isEnabledLinkedFile() = (features and IORING_FEAT_LINKED_FILE.toUInt()) != 0u

    /**
     * Checks if io_uring supports calling io_uring_register(2) using a registered ring fd.
     * Available since kernel 6.3.
     */
    fun isEnabledRegRegRing() = (features and IORING_FEAT_REG_REG_RING.toUInt()) != 0u

    override fun toString(): String {
        return buildString {
            append("Ring Features:\n")
            append("Feature                       | Enabled\n")
            append("----------------------------------------\n")
            append("Single MMap                   | ${isEnabledSingleMMap()}\n")
            append("No Drop                       | ${isEnabledNoDrop()}\n")
            append("Submit Stable                 | ${isEnabledSubmitStable()}\n")
            append("RW Current Position           | ${isEnabledRWCurPos()}\n")
            append("Current Personality           | ${isEnabledCurPersonality()}\n")
            append("Fast Poll                     | ${isEnabledFastPoll()}\n")
            append("Poll 32 Bits                  | ${isEnabledPoll32Bits()}\n")
            append("SQ Poll Non Fixed             | ${isEnabledSQPollNonFixed()}\n")
            append("Enter Extended Argument       | ${isEnabledEnterExtArg()}\n")
            append("Native Workers                | ${isEnabledNativeWorkers()}\n")
            append("Resource Tags                 | ${isEnabledRSRCTags()}\n")
            append("CQE Skip                      | ${isEnabledCQESkip()}\n")
            append("Linked File                   | ${isEnabledLinkedFile()}\n")
            append("Register Ring                 | ${isEnabledRegRegRing()}\n")
        }
    }

    internal companion object {
        const val IORING_FEAT_SINGLE_MMAP = 1 shl 0
        const val IORING_FEAT_NODROP = 1 shl 1
        const val IORING_FEAT_SUBMIT_STABLE = 1 shl 2
        const val IORING_FEAT_RW_CUR_POS = 1 shl 3
        const val IORING_FEAT_CUR_PERSONALITY = 1 shl 4
        const val IORING_FEAT_FAST_POLL = 1 shl 5
        const val IORING_FEAT_POLL_32BITS = 1 shl 6
        const val IORING_FEAT_SQPOLL_NONFIXED = 1 shl 7
        const val IORING_FEAT_ENTER_EXT_ARG = 1 shl 8
        const val IORING_FEAT_NATIVE_WORKERS = 1 shl 9
        const val IORING_FEAT_RSRC_TAGS = 1 shl 10
        const val IORING_FEAT_CQE_SKIP = 1 shl 11
        const val IORING_FEAT_LINKED_FILE = 1 shl 12
        const val IORING_FEAT_REG_REG_RING = 1 shl 13
    }
}
