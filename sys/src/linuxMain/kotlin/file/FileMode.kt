package file

value class FileMode constructor(val mode: UInt) {

    class Builder {
        private var mode: UInt = 0u

        init {
            ownerRead()
            ownerWrite()
        }

        fun ownerRead(): Builder {
            mode = mode or S_IRUSR
            return this
        }

        fun ownerWrite(): Builder {
            mode = mode or S_IWUSR
            return this
        }

        fun ownerExecute(): Builder {
            mode = mode or S_IXUSR
            return this
        }

        fun groupRead(): Builder {
            mode = mode or S_IRGRP
            return this
        }

        fun groupWrite(): Builder {
            mode = mode or S_IWGRP
            return this
        }

        fun groupExecute(): Builder {
            mode = mode or S_IXGRP
            return this
        }

        fun othersRead(): Builder {
            mode = mode or S_IROTH
            return this
        }

        fun othersWrite(): Builder {
            mode = mode or S_IWOTH
            return this
        }

        fun othersExecute(): Builder {
            mode = mode or S_IXOTH
            return this
        }

        fun build(): FileMode {
            return FileMode(mode)
        }
    }

    companion object {
        const val S_IRUSR = 0b100_000_000u
        const val S_IWUSR = 0b010_000_000u
        const val S_IXUSR = 0b001_000_000u
        const val S_IRGRP = 0b000_100_000u
        const val S_IWGRP = 0b000_010_000u
        const val S_IXGRP = 0b000_001_000u
        const val S_IROTH = 0b000_000_100u
        const val S_IWOTH = 0b000_000_010u
        const val S_IXOTH = 0b000_000_001u
    }
}
