import kotlinx.cinterop.*
import platform.posix.*
import liburing.*



fun main() = memScoped {
    val fileName = "test.txt"
    val fd = open(fileName, O_RDONLY)

    val ring = memScoped {
        val ringPtr = alloc<open_how>()
        ringPtr.mode = 0uL
        ringPtr.flags = 256uL
        ringPtr.resolve = 0uL

        if (ringPtr == null) {
            println("Failed to initialize the ring")
            return
        }
        ringPtr
    }

    val sqe = memScoped {
        val sqePtr = alloc<io_uring_sqe>()
        if (sqePtr == null) {
            println("Failed to get SQE")
            return
        }
        sqePtr
    }
}
