package init

import kotlin.test.Test
import kotlin.test.assertEquals

internal class RingFlagsTest {

    @Test
    fun `enabling io poll sets the correct bit`() {
        val result = ringFlags {
            enableIOPoll()
        }.flags

        assertEquals(1u, result)
    }

    @Test
    fun `enabling sq poll sets the correct bit`() {
        val result = ringFlags {
            enableSQPoll()
        }.flags

        assertEquals(2u, result)
    }

    @Test
    fun `enabling sq aff sets the correct bit`() {
        val result = ringFlags {
            enableSQAff()
        }.flags

        assertEquals(4u, result)
    }

    @Test
    fun `enabling cq size sets the correct bit`() {
        val result = ringFlags {
            enableCQSize()
        }.flags

        assertEquals(8u, result)
    }

    @Test
    fun `enabling clamp sets the correct bit`() {
        val result = ringFlags {
            enableClamp()
        }.flags

        assertEquals(16u, result)
    }

    @Test
    fun `enabling attach wq sets the correct bit`() {
        val result = ringFlags {
            enableAttachWQ()
        }.flags

        assertEquals(32u, result)
    }

    @Test
    fun `enabling r disabled sets the correct bit`() {
        val result = ringFlags {
            enableRDisabled()
        }.flags

        assertEquals(64u, result)
    }

    @Test
    fun `enabling submit all sets the correct bit`() {
        val result = ringFlags {
            enableSubmitAll()
        }.flags

        assertEquals(128u, result)
    }

    @Test
    fun `enabling coop task run sets the correct bit`() {
        val result = ringFlags {
            enableCoopTaskRun()
        }.flags

        assertEquals(256u, result)
    }

    @Test
    fun `enabling task run flag sets the correct bit`() {
        val result = ringFlags {
            enableTaskRunFlag()
        }.flags

        assertEquals(512u, result)
    }

    @Test
    fun `enabling sqe128 sets the correct bit`() {
        val result = ringFlags {
            enableSQE128()
        }.flags

        assertEquals(1024u, result)
    }

    @Test
    fun `enabling cqe32 sets the correct bit`() {
        val result = ringFlags {
            enableCQE32()
        }.flags

        assertEquals(2048u, result)
    }

    @Test
    fun `enabling single issuer sets the correct bit`() {
        val result = ringFlags {
            enableSingleIssuer()
        }.flags

        assertEquals(4096u, result)
    }

    @Test
    fun `enabling defer task run sets the correct bit`() {
        val result = ringFlags {
            enableDeferTaskRun()
        }.flags

        assertEquals(8192u, result)
    }

    @Test
    fun `enabling all flags sets the correct bits`() {
        val result = ringFlags {
            enableIOPoll()
            enableSQPoll()
            enableSQAff()
            enableCQSize()
            enableClamp()
            enableAttachWQ()
            enableRDisabled()
            enableSubmitAll()
            enableCoopTaskRun()
            enableTaskRunFlag()
            enableSQE128()
            enableCQE32()
            enableSingleIssuer()
            enableDeferTaskRun()
        }.flags

        assertEquals(0b11111111111111u, result)
    }
}
