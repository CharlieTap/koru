package probe

import probe.ProbeResult.Success

sealed class ProbeResult {
    data class Success(val opCodes: Set<IORingOp>): ProbeResult()
    data object ProbeUnsupported: ProbeResult()
}

fun interface ProbeSupportedOpCodes: () -> ProbeResult

fun probeSupportedOpCodes() = ProbeSupportedOpCodes {
    probeSupportedOpCodes(IOUringProbeInterface)
}()

internal fun probeSupportedOpCodes(
    probeable: ProbeInterface = IOUringProbeInterface,
): ProbeResult {
    val probe = probeable.getProbe() ?: return ProbeResult.ProbeUnsupported
    return IORingOp.entries.filter { opCode ->
        probeable.probeSupportForOpCode(probe, opCode.ordinal) != 0
    }.toSet().let(::Success).also {
        probeable.freeProbe(probe)
    }
}
