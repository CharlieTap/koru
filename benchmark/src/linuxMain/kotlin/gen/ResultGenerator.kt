package gen

import benchmark.BenchmarkResult

object ResultGenerator: (BenchmarkResult) -> String {

    override fun invoke(result: BenchmarkResult): String {

        val ioType = if(result.flags.isDirect()) {
            "Direct"
        } else "Buffered"

        val ioBlockingType = if(result.flags.isBlocking()) {
            "Blocking"
        } else "Non Blocking"

        return """
        Benchmark performed ${result.numberOfSysCalls} with a buffer size of ${result.bufferSize} using $ioType $ioBlockingType IO in ${result.duration.inWholeMicroseconds} microseconds
        """.trimIndent()
    }
}