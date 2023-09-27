package file

value class FileDescriptorFlags constructor(val flags: Int) {

    fun isBlocking(): Boolean = flags and O_NONBLOCK == 0
    fun isNonBlocking(): Boolean = flags and O_NONBLOCK != 0
    fun isBuffered(): Boolean = flags and O_DIRECT == 0
    fun isDirect(): Boolean = flags and O_DIRECT != 0
    fun isCreateIfNotExists(): Boolean = flags and O_CREAT != 0

    class Builder {
        private var flags: Int = 0

        init {
            readOnly()
            blocking()
            buffered()
            doNotCreateIfNotExists()
        }

        fun direct(): Builder {
            flags = flags or O_DIRECT
            return this
        }

        fun buffered(): Builder {
            flags = flags and O_DIRECT.inv()
            return this
        }

        fun nonBlocking(): Builder {
            flags = flags or O_NONBLOCK
            return this
        }

        fun blocking(): Builder {
            flags = flags and O_NONBLOCK.inv()
            return this
        }

        fun createIfNotExists(): Builder {
            flags = flags or O_CREAT
            return this
        }

        fun doNotCreateIfNotExists(): Builder {
            flags = flags and O_CREAT.inv()
            return this
        }

        fun readOnly(): Builder {
            flags = (flags and O_WRONLY.inv() and O_RDWR.inv()) or O_RDONLY
            return this
        }

        fun writeOnly(): Builder {
            flags = (flags and O_RDONLY.inv() and O_RDWR.inv()) or O_WRONLY
            return this
        }

        fun readWrite(): Builder {
            flags = (flags and O_RDONLY.inv() and O_WRONLY.inv()) or O_RDWR
            return this
        }

        fun executable(): Builder {
            flags = flags or O_EXEC
            return this
        }

        fun build(): FileDescriptorFlags {
            return FileDescriptorFlags(flags)
        }
    }

    internal companion object {
        const val O_RDONLY = 0
        const val O_WRONLY = 1
        const val O_RDWR = 2
        const val O_EXEC = 4
        const val O_CREAT = 64
        const val O_NONBLOCK = 2048
        const val O_DIRECT = 16384
    }
}
