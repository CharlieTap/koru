package queue.sqe

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.pointed
import liburing.IOSQE_FIXED_FILE
import liburing.IOSQE_IO_LINK
import liburing.io_uring_sqe

value class SubmissionQueueEntry(internal val sqe: CPointer<io_uring_sqe>) {

    fun setUserData(data: Long) {
        sqe.pointed.apply {
            user_data = data.toULong()
        }
    }

    fun setLinked() {
        sqe.pointed.apply {
            flags = flags or IOSQE_IO_LINK.toUByte()
        }
    }

    fun setFixedFile() {
        sqe.pointed.apply {
            flags = flags or IOSQE_FIXED_FILE.toUByte()
        }
    }

    override fun toString(): String {
        val struct = sqe.pointed
        return buildString {
            append("SubmissionQueueEntry {\n")
            append("  opcode: ${struct.opcode},\n")
            append("  flags: ${struct.flags},\n")
            append("  ioprio: ${struct.ioprio},\n")
            append("  fd: ${struct.fd},\n")
            append("  off: ${struct.off},\n")
            append("  addr: ${struct.addr},\n")
            append("  len: ${struct.len},\n")
            append("  user_data: ${struct.user_data},\n")
            append("  buf_index: ${struct.buf_index},\n")
            append("  buf_group: ${struct.buf_group},\n")
            append("  personality: ${struct.personality},\n")
            append("  file_index: ${struct.file_index},\n")
            append("  splice_fd_in: ${struct.splice_fd_in},\n")
            append("  splice_flags: ${struct.splice_flags},\n")
            append("  splice_off_in: ${struct.splice_off_in},\n")
            append("  accept_flags: ${struct.accept_flags},\n")
            append("  cancel_flags: ${struct.cancel_flags},\n")
            append("  open_flags: ${struct.open_flags},\n")
            append("  unlink_flags: ${struct.unlink_flags},\n")
            append("  statx_flags: ${struct.statx_flags},\n")
            append("  timeout_flags: ${struct.timeout_flags},\n")
            append("  rename_flags: ${struct.rename_flags},\n")
            append("  poll_events: ${struct.poll_events},\n")
            append("  poll32_events: ${struct.poll32_events},\n")
            append("  fsync_flags: ${struct.fsync_flags},\n")
            append("  sync_range_flags: ${struct.sync_range_flags},\n")
            append("  fadvise_advice: ${struct.fadvise_advice},\n")
            append("  msg_flags: ${struct.msg_flags},\n")
            append("  hardlink_flags: ${struct.hardlink_flags},\n")
            append("  rw_flags: ${struct.rw_flags},\n")
            append("  addr2: ${struct.addr2}\n")
            append("}")
        }
    }
}
