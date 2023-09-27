package ring

import kotlinx.cinterop.*
import liburing.*
import kotlin.test.Test
import kotlin.test.assertEquals

internal class SubmitRingTest {

    private fun fakeInterface(result: Int): SubmitRingInterface {
        return object : SubmitRingInterface {
            override fun submit(ring: CValuesRef<io_uring>): Int {
                return result
            }
        }
    }

    @Test
    fun `calls through to submit successfully`() = memScoped {
        val expectedRing = Ring(alloc<io_uring>().ptr)
        val expectedResult = 1

        val ringInterface = object : SubmitRingInterface {
            override fun submit(ring: CValuesRef<io_uring>): Int {
                assertEquals(expectedRing.ring, ring)
                return expectedResult
            }
        }

        val result = submitRing(expectedRing, ringInterface)
        assertEquals(SubmitRingResult.Submitted(expectedResult), result)
    }

    @Test
    fun `submitRing returns InvalidArgument when EINVAL is returned`() = memScoped {
        val ring = Ring(alloc<io_uring>().ptr)
        val result = submitRing(ring, fakeInterface(-EINVAL))
        assertEquals(SubmitRingResult.Failure.InvalidArgument, result)
    }

    @Test
    fun `submitRing returns InvalidFileDescriptor when EBADF is returned`() = memScoped {
        val ring = Ring(alloc<io_uring>().ptr)
        val result = submitRing(ring, fakeInterface(-EBADF))
        assertEquals(SubmitRingResult.Failure.InvalidFileDescriptor, result)
    }

    @Test
    fun `submitRing returns NoSpaceLeft when ENOSPC is returned`() = memScoped {
        val ring = Ring(alloc<io_uring>().ptr)
        val result = submitRing(ring, fakeInterface(-ENOSPC))
        assertEquals(SubmitRingResult.Failure.NoSpaceLeft, result)
    }

    @Test
    fun `submitRing returns BadAddress when EFAULT is returned`() = memScoped {
        val ring = Ring(alloc<io_uring>().ptr)
        val result = submitRing(ring, fakeInterface(-EFAULT))
        assertEquals(SubmitRingResult.Failure.BadAddress, result)
    }

    @Test
    fun `submitRing returns PermissionDenied when EPERM is returned`() = memScoped {
        val ring = Ring(alloc<io_uring>().ptr)
        val result = submitRing(ring, fakeInterface(-EPERM))
        assertEquals(SubmitRingResult.Failure.PermissionDenied, result)
    }

    @Test
    fun `submitRing returns ResourceTemporarilyUnavailable when EAGAIN is returned`() = memScoped {
        val ring = Ring(alloc<io_uring>().ptr)
        val result = submitRing(ring, fakeInterface(-EAGAIN))
        assertEquals(SubmitRingResult.Failure.ResourceTemporarilyUnavailable, result)
    }

    @Test
    fun `submitRing returns OutOfMemory when ENOMEM is returned`() = memScoped {
        val ring = Ring(alloc<io_uring>().ptr)
        val result = submitRing(ring, fakeInterface(-ENOMEM))
        assertEquals(SubmitRingResult.Failure.OutOfMemory, result)
    }

    @Test
    fun `submitRing returns Other when an unknown error is returned`() = memScoped {
        val ring = Ring(alloc<io_uring>().ptr)
        val unknownErrorCode = -999
        val result = submitRing(ring, fakeInterface(unknownErrorCode))
        assertEquals(SubmitRingResult.Failure.Other(unknownErrorCode), result)
    }
}
