package init

import alloc.testAllocator
import init.InitRingResult.Failure
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CValuesRef
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.pointed
import liburing.*
import kotlin.test.Test
import kotlin.test.assertEquals

internal class InitRingWithParamsTest {

    private companion object {
        private const val ENTRIES = 0u
    }

    private fun fakeInterface(
        result: Int,
        kernelParamsApplicator: io_uring_params.() -> Unit = {}
    ): InitInterface {
        return object : InitInterface {
            override fun initialiseQueue(queueSize: UInt, ring: CValuesRef<io_uring>, flags: UInt): Int {
                return result
            }

            override fun initialiseQueueWithParams(queueSize: UInt, ring: CValuesRef<io_uring>, params: CPointer<io_uring_params>): Int {
                params.pointed.apply(kernelParamsApplicator)
                return result
            }
        }
    }

    private val ringEntries by lazy {
        RingEntries(ENTRIES)
    }

    @Test
    fun `test successful initialization`() = memScoped {

        val ringFlags = RingFlags.Builder().enableIOPoll().build()
        val workingQueueFileDescriptor = WorkQueueFileDescriptor(117u)
        val sqThreadCpu = SQThreadCpu(12u)
        val sqThreadIdleInMillis = SQThreadIdleInMillis(200u)
        val configuration = RingConfiguration(
            sqEntries = ringEntries.entries,
            cqEntries = ringEntries.entries,
            flags = ringFlags,
            wqFd = workingQueueFileDescriptor,
            sqThreadCpu = sqThreadCpu,
            sqThreadIdle = sqThreadIdleInMillis
        )

        val ringFeatures = RingFeatures(2048u)

        val expectedDetails = RingDetails(
            sqEntries = ringEntries.entries,
            cqEntries = ringEntries.entries,
            features = ringFeatures,
            wqFd = workingQueueFileDescriptor
        )

        val fakeInterface = fakeInterface(0) {
            sq_entries = ringEntries.entries
            cq_entries = ringEntries.entries
            features = ringFeatures.features
            flags = ringFlags.flags
            wq_fd = workingQueueFileDescriptor.fd
            sq_thread_cpu = sqThreadCpu.cpu
            sq_thread_idle = sqThreadIdleInMillis.millis
        }

        val result = initRing(ringEntries, configuration, fakeInterface, testAllocator())

        assertEquals(expectedDetails, (result as InitRingResult.Success).details)
    }

    @Test
    fun `test invalid argument error`() = memScoped {
        val result = initRing(ringEntries, null, fakeInterface(EINVAL), testAllocator())

        assertEquals(Failure.InvalidArgument, result)
    }

    @Test
    fun `test insufficient memory error`() = memScoped {
        val result = initRing(ringEntries, null, fakeInterface(ENOMEM), testAllocator())
        assertEquals(Failure.InsufficientMemory, result)
    }

    @Test
    fun `test temporary resource shortage error`() = memScoped {
        val result = initRing(ringEntries, null, fakeInterface(EAGAIN), testAllocator())
        assertEquals(Failure.TemporaryResourceShortage, result)
    }

    @Test
    fun `test invalid pointer error`() = memScoped {
        val result = initRing(ringEntries, null, fakeInterface(EFAULT), testAllocator())
        assertEquals(Failure.InvalidPointer, result)
    }

    @Test
    fun `test file descriptor limit reached error`() = memScoped {
        val result = initRing(ringEntries, null, fakeInterface(EMFILE), testAllocator())
        assertEquals(Failure.FileDescriptorLimitReached, result)
    }

    @Test
    fun `test other error`() = memScoped {
        val errCode = 999
        val result = initRing(ringEntries, null, fakeInterface(errCode), testAllocator())
        assertEquals(Failure.Other(errCode), result)
    }
}
