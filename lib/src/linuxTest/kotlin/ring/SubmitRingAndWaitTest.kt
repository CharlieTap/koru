package ring

import kotlinx.cinterop.*
import liburing.*
import kotlin.test.Test
import kotlin.test.assertEquals

internal class SubmitRingAndWaitTest {

    private fun fakeInterface(result: Int): SubmitRingAndWaitInterface {
        return object : SubmitRingAndWaitInterface {
            override fun submitAndWait(
                ring: CValuesRef<io_uring>,
                completionEventsToWaitFor: UInt,
            ): Int {
                return result
            }
        }
    }

    @Test
    fun `calls through to submit successfully`() = memScoped {
        val expectedRing = Ring(alloc<io_uring>().ptr)
        val expectedResult = 1

        val ringInterface = object : SubmitRingAndWaitInterface {
            override fun submitAndWait(
                ring: CValuesRef<io_uring>,
                completionEventsToWaitFor: UInt,
            ): Int {
                assertEquals(expectedRing.ring, ring)
                return expectedResult
            }
        }

        val result = submitRingAndWait(expectedRing, 1, ringInterface)
        assertEquals(SubmitRingResult.Submitted(expectedResult), result)
    }

    @Test
    fun `submitRingAndWait returns InvalidArgument when EINVAL is returned`() = memScoped {
        val ring = Ring(alloc<io_uring>().ptr)
        val result = submitRingAndWait(ring, 1, fakeInterface(-EINVAL))
        assertEquals(SubmitRingResult.Failure.InvalidArgument, result)
    }

    @Test
    fun `submitRingAndWait returns InvalidFileDescriptor when EBADF is returned`() = memScoped {
        val ring = Ring(alloc<io_uring>().ptr)
        val result = submitRingAndWait(ring, 1, fakeInterface(-EBADF))
        assertEquals(SubmitRingResult.Failure.InvalidFileDescriptor, result)
    }

    @Test
    fun `submitRingAndWait returns NoSpaceLeft when ENOSPC is returned`() = memScoped {
        val ring = Ring(alloc<io_uring>().ptr)
        val result = submitRingAndWait(ring, 1, fakeInterface(-ENOSPC))
        assertEquals(SubmitRingResult.Failure.NoSpaceLeft, result)
    }

    @Test
    fun `submitRingAndWait returns BadAddress when EFAULT is returned`() = memScoped {
        val ring = Ring(alloc<io_uring>().ptr)
        val result = submitRingAndWait(ring, 1, fakeInterface(-EFAULT))
        assertEquals(SubmitRingResult.Failure.BadAddress, result)
    }

    @Test
    fun `submitRingAndWait returns PermissionDenied when EPERM is returned`() = memScoped {
        val ring = Ring(alloc<io_uring>().ptr)
        val result = submitRingAndWait(ring, 1, fakeInterface(-EPERM))
        assertEquals(SubmitRingResult.Failure.PermissionDenied, result)
    }

    @Test
    fun `submitRingAndWait returns ResourceTemporarilyUnavailable when EAGAIN is returned`() = memScoped {
        val ring = Ring(alloc<io_uring>().ptr)
        val result = submitRingAndWait(ring, 1, fakeInterface(-EAGAIN))
        assertEquals(SubmitRingResult.Failure.ResourceTemporarilyUnavailable, result)
    }

    @Test
    fun `submitRingAndWait returns OutOfMemory when ENOMEM is returned`() = memScoped {
        val ring = Ring(alloc<io_uring>().ptr)
        val result = submitRingAndWait(ring, 1, fakeInterface(-ENOMEM))
        assertEquals(SubmitRingResult.Failure.OutOfMemory, result)
    }

    @Test
    fun `submitRingAndWait returns Other when an unknown error is returned`() = memScoped {
        val ring = Ring(alloc<io_uring>().ptr)
        val unknownErrorCode = -999
        val result = submitRingAndWait(ring, 1, fakeInterface(unknownErrorCode))
        assertEquals(SubmitRingResult.Failure.Other(unknownErrorCode), result)
    }
}
