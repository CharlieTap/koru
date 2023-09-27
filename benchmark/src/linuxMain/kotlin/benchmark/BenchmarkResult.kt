package benchmark

import file.FileDescriptorFlags
import kotlin.time.Duration

data class BenchmarkResult(
    val bufferSize: Long,
    val flags: FileDescriptorFlags,
    val numberOfSysCalls: Int,
    val duration: Duration
)
