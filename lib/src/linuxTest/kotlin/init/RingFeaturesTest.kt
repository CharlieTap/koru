package init

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertFalse

internal class RingFeaturesTest {

    @Test
    fun testIsEnabledSingleMMap() {
        val ringFeaturesEnabled = RingFeatures(RingFeatures.IORING_FEAT_SINGLE_MMAP.toUInt())
        assertTrue(ringFeaturesEnabled.isEnabledSingleMMap())

        val ringFeaturesDisabled = RingFeatures(0u)
        assertFalse(ringFeaturesDisabled.isEnabledSingleMMap())
    }

    @Test
    fun testIsEnabledNoDrop() {
        val ringFeaturesEnabled = RingFeatures(RingFeatures.IORING_FEAT_NODROP.toUInt())
        assertTrue(ringFeaturesEnabled.isEnabledNoDrop())

        val ringFeaturesDisabled = RingFeatures(0u)
        assertFalse(ringFeaturesDisabled.isEnabledNoDrop())
    }

    @Test
    fun testIsEnabledSubmitStable() {
        val ringFeaturesEnabled = RingFeatures(RingFeatures.IORING_FEAT_SUBMIT_STABLE.toUInt())
        assertTrue(ringFeaturesEnabled.isEnabledSubmitStable())

        val ringFeaturesDisabled = RingFeatures(0u)
        assertFalse(ringFeaturesDisabled.isEnabledSubmitStable())
    }

    @Test
    fun testIsEnabledRWCurPos() {
        val ringFeaturesEnabled = RingFeatures(RingFeatures.IORING_FEAT_RW_CUR_POS.toUInt())
        assertTrue(ringFeaturesEnabled.isEnabledRWCurPos())

        val ringFeaturesDisabled = RingFeatures(0u)
        assertFalse(ringFeaturesDisabled.isEnabledRWCurPos())
    }

    @Test
    fun testIsEnabledCurPersonality() {
        val ringFeaturesEnabled = RingFeatures(RingFeatures.IORING_FEAT_CUR_PERSONALITY.toUInt())
        assertTrue(ringFeaturesEnabled.isEnabledCurPersonality())

        val ringFeaturesDisabled = RingFeatures(0u)
        assertFalse(ringFeaturesDisabled.isEnabledCurPersonality())
    }

    @Test
    fun testIsEnabledFastPoll() {
        val ringFeaturesEnabled = RingFeatures(RingFeatures.IORING_FEAT_FAST_POLL.toUInt())
        assertTrue(ringFeaturesEnabled.isEnabledFastPoll())

        val ringFeaturesDisabled = RingFeatures(0u)
        assertFalse(ringFeaturesDisabled.isEnabledFastPoll())
    }

    @Test
    fun testIsEnabledPoll32Bits() {
        val ringFeaturesEnabled = RingFeatures(RingFeatures.IORING_FEAT_POLL_32BITS.toUInt())
        assertTrue(ringFeaturesEnabled.isEnabledPoll32Bits())

        val ringFeaturesDisabled = RingFeatures(0u)
        assertFalse(ringFeaturesDisabled.isEnabledPoll32Bits())
    }

    @Test
    fun testIsEnabledSQPollNonFixed() {
        val ringFeaturesEnabled = RingFeatures(RingFeatures.IORING_FEAT_SQPOLL_NONFIXED.toUInt())
        assertTrue(ringFeaturesEnabled.isEnabledSQPollNonFixed())

        val ringFeaturesDisabled = RingFeatures(0u)
        assertFalse(ringFeaturesDisabled.isEnabledSQPollNonFixed())
    }

    @Test
    fun testIsEnabledEnterExtArg() {
        val ringFeaturesEnabled = RingFeatures(RingFeatures.IORING_FEAT_ENTER_EXT_ARG.toUInt())
        assertTrue(ringFeaturesEnabled.isEnabledEnterExtArg())

        val ringFeaturesDisabled = RingFeatures(0u)
        assertFalse(ringFeaturesDisabled.isEnabledEnterExtArg())
    }

    @Test
    fun testIsEnabledNativeWorkers() {
        val ringFeaturesEnabled = RingFeatures(RingFeatures.IORING_FEAT_NATIVE_WORKERS.toUInt())
        assertTrue(ringFeaturesEnabled.isEnabledNativeWorkers())

        val ringFeaturesDisabled = RingFeatures(0u)
        assertFalse(ringFeaturesDisabled.isEnabledNativeWorkers())
    }

    @Test
    fun testIsEnabledRSRCTags() {
        val ringFeaturesEnabled = RingFeatures(RingFeatures.IORING_FEAT_RSRC_TAGS.toUInt())
        assertTrue(ringFeaturesEnabled.isEnabledRSRCTags())

        val ringFeaturesDisabled = RingFeatures(0u)
        assertFalse(ringFeaturesDisabled.isEnabledRSRCTags())
    }

    @Test
    fun testIsEnabledCQESkip() {
        val ringFeaturesEnabled = RingFeatures(RingFeatures.IORING_FEAT_CQE_SKIP.toUInt())
        assertTrue(ringFeaturesEnabled.isEnabledCQESkip())

        val ringFeaturesDisabled = RingFeatures(0u)
        assertFalse(ringFeaturesDisabled.isEnabledCQESkip())
    }

    @Test
    fun testIsEnabledLinkedFile() {
        val ringFeaturesEnabled = RingFeatures(RingFeatures.IORING_FEAT_LINKED_FILE.toUInt())
        assertTrue(ringFeaturesEnabled.isEnabledLinkedFile())

        val ringFeaturesDisabled = RingFeatures(0u)
        assertFalse(ringFeaturesDisabled.isEnabledLinkedFile())
    }

    @Test
    fun testIsEnabledRegRegRing() {
        val ringFeaturesEnabled = RingFeatures(RingFeatures.IORING_FEAT_REG_REG_RING.toUInt())
        assertTrue(ringFeaturesEnabled.isEnabledRegRegRing())

        val ringFeaturesDisabled = RingFeatures(0u)
        assertFalse(ringFeaturesDisabled.isEnabledRegRegRing())
    }
}
