package prep.close

import kotlinx.cinterop.CValuesRef
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import liburing.io_uring_sqe
import queue.sqe.SubmissionQueueEntry
import kotlin.test.Test
import kotlin.test.assertEquals

internal class PrepareCloseDirectTest {

    @Test
    fun `calls through to prepare close direct successfully`() = memScoped {
        val sqe = alloc<io_uring_sqe>()
        val expectedSubmissionQueueEntry = SubmissionQueueEntry(sqe.ptr)
        val expectedFileIndex= 117

        val closeInterface = object : PrepCloseDirectInterface {
            override fun prepCloseDirect(sqe: CValuesRef<io_uring_sqe>, fileIndex: UInt) {
                assertEquals(expectedSubmissionQueueEntry.sqe, sqe)
                assertEquals(expectedFileIndex.toUInt(), fileIndex)
            }
        }

        prepareCloseDirect(
            expectedSubmissionQueueEntry,
            expectedFileIndex,
            closeInterface
        )
    }
}
