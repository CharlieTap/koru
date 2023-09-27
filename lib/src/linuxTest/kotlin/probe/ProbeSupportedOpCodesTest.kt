package probe

import kotlinx.cinterop.*
import liburing.io_uring_probe
import kotlin.test.Test
import kotlin.test.assertEquals

internal class ProbeSupportedOpCodesTest {

    @Test
    fun `probe returns as unsupported if get probe is null`() {
        val probeInterface = object : ProbeInterface {
            override fun getProbe(): CPointer<io_uring_probe>? = null
            override fun freeProbe(probe: CPointer<io_uring_probe>) = Unit
            override fun probeSupportForOpCode(probe: CPointer<io_uring_probe>, opCode: Int): Int = 0
        }

        val result = probeSupportedOpCodes(probeInterface)

        assertEquals(ProbeResult.ProbeUnsupported, result)
    }

    @Test
    fun `probe returns with only supported opcodes`() = memScoped {
        val probe = alloc<io_uring_probe>()
        val supportedOpCodes = setOf(
            IORingOp.FADVISE,
            IORingOp.CONNECT,
        )
        val probeInterface = object : ProbeInterface {
            var freed = false
            override fun getProbe(): CPointer<io_uring_probe> = probe.ptr
            override fun freeProbe(probe: CPointer<io_uring_probe>) { freed = true }
            override fun probeSupportForOpCode(probe: CPointer<io_uring_probe>, opCode: Int): Int =
                if(supportedOpCodes.map(IORingOp::ordinal).contains(opCode)) 1 else 0
        }

        val result = probeSupportedOpCodes(probeInterface)

        assertEquals(ProbeResult.Success(supportedOpCodes), result)
        assertEquals(true, probeInterface.freed)
    }
}
