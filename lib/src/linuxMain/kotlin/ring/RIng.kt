package ring

import kotlinx.cinterop.CArrayPointer
import kotlinx.cinterop.CValuesRef
import kotlinx.cinterop.IntVar
import liburing.io_uring

value class Ring internal constructor(internal val ring: CValuesRef<io_uring>)