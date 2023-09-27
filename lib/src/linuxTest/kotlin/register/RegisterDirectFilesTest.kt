package register

import kotlinx.cinterop.*
import liburing.io_uring
import kotlin.test.Test
import ring.Ring
import kotlin.test.assertEquals

internal class RegisterDirectFilesTest {

    @Test
    fun `successful register of direct files`() = memScoped {
        val expectedRing = Ring(alloc<io_uring>().ptr)
        val expectedNumberOfDirectFiles = 10

        val registerInterface = object : RegisterFilesInterface {
            override fun registerFiles(ring: CValuesRef<io_uring>?, files: CArrayPointer<IntVar>?, count: UInt): Int {
                assertEquals(expectedRing.ring, ring)
                assertEquals(expectedNumberOfDirectFiles.toUInt(), count)
                return 0
            }
        }

        val result = registerDirectFiles(expectedRing, expectedNumberOfDirectFiles, registerInterface)
        assertEquals(RegisterDirectFilesResult.Success, result)
    }

    @Test
    fun `unsuccessful register of direct files`() = memScoped {
        val expectedRing = Ring(alloc<io_uring>().ptr)
        val expectedNumberOfDirectFiles = 10
        val expectedErrNo = -1

        val registerInterface = object : RegisterFilesInterface {
            override fun registerFiles(ring: CValuesRef<io_uring>?, files: CArrayPointer<IntVar>?, count: UInt): Int {
                assertEquals(expectedRing.ring, ring)
                assertEquals(expectedNumberOfDirectFiles.toUInt(), count)
                return expectedErrNo
            }
        }

        val result = registerDirectFiles(expectedRing, expectedNumberOfDirectFiles, registerInterface)
        assertEquals(RegisterDirectFilesResult.Failure(expectedErrNo), result)
    }
}
