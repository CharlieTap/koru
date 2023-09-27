package file

value class FileDescriptor(val fd: Int) {
    companion object {
        val AT_FDCWD = FileDescriptor(-100)
        val STDIN = FileDescriptor(0)
        val STDOUT = FileDescriptor(1)
        val STDERR = FileDescriptor(2)
    }
}
