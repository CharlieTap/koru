package probe

import kotlinx.cinterop.CPointer
import liburing.io_uring_free_probe
import liburing.io_uring_get_probe
import liburing.io_uring_opcode_supported
import liburing.io_uring_probe

internal interface ProbeInterface {
    fun getProbe(): CPointer<io_uring_probe>?
    fun freeProbe(probe: CPointer<io_uring_probe>)
    fun probeSupportForOpCode(probe: CPointer<io_uring_probe>, opCode: Int): Int
}

internal object IOUringProbeInterface: ProbeInterface {
    override fun getProbe(): CPointer<io_uring_probe>? =
        io_uring_get_probe()

    override fun freeProbe(probe: CPointer<io_uring_probe>) =
        io_uring_free_probe(probe)

    override fun probeSupportForOpCode(probe: CPointer<io_uring_probe>, opCode: Int) =
        io_uring_opcode_supported(probe, opCode)
}
