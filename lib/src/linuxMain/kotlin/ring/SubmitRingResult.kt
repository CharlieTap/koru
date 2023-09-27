package ring

import liburing.*

sealed class SubmitRingResult {
    data class Submitted(val sqesSubmitted: Int): SubmitRingResult()

    sealed class Failure: SubmitRingResult() {
        data object InvalidEntry : Failure()
        data object InvalidFileDescriptor : Failure()
        data object NoSpaceLeft : Failure()
        data object BadAddress : Failure()
        data object PermissionDenied : Failure()
        data object ResourceUnavailable : Failure()
        data object InvalidArgument : Failure()
        data object InterruptedBySignal : Failure()
        data object OperationNotPermitted : Failure()
        data object ResourceTemporarilyUnavailable : Failure()
        data object OutOfMemory : Failure()
        data class Other(val err: Int): Failure()
    }

    companion object {
        fun fromReturnCode(code: Int): SubmitRingResult {
            return if (code >= 0) {
                Submitted(code)
            } else {
                when (code) {
                    -EINVAL -> Failure.InvalidArgument
                    -EBADF -> Failure.InvalidFileDescriptor
                    -ENOSPC -> Failure.NoSpaceLeft
                    -EFAULT -> Failure.BadAddress
                    -EPERM -> Failure.PermissionDenied
                    -EAGAIN -> Failure.ResourceTemporarilyUnavailable
                    -ENOMEM -> Failure.OutOfMemory
                    else -> Failure.Other(code)
                }
            }
        }
    }
}
