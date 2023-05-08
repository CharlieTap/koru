
import kotlinx.cinterop.staticCFunction
import platform.posix.SIGINT
import platform.posix.SIGTERM
import platform.posix.signal

fun main() {
    setupSignalHandler()
    println("hello11")
}

fun setupSignalHandler() {
    val handler = staticCFunction { _: Int ->
        println("Gracefully shutting down")

    }

    signal(SIGTERM, handler)
    signal(SIGINT, handler)
}
