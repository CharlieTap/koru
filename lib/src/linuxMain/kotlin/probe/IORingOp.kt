package probe

enum class IORingOp {
    NOP,              // No operation; can be used for testing purposes
    READV,            // Read operation using scatter/gather vectors
    WRITEV,           // Write operation using scatter/gather vectors
    FSYNC,            // Synchronize file's in-core state with storage device
    READ_FIXED,       // Read operation with fixed buffers
    WRITE_FIXED,      // Write operation with fixed buffers
    POLL_ADD,         // Add a file descriptor to be monitored by poll
    POLL_REMOVE,      // Remove a file descriptor being monitored by poll
    SYNC_FILE_RANGE,  // Synchronize a file segment with disk
    SENDMSG,          // Send a message on a socket
    RECVMSG,          // Receive a message from a socket
    TIMEOUT,          // Specify a timeout for an operation
    TIMEOUT_REMOVE,   // Remove a previously specified timeout
    ACCEPT,           // Accept a new connection on a socket
    ASYNC_CANCEL,     // Cancel an outstanding asynchronous operation
    LINK_TIMEOUT,     // Link a timeout command to an existing operation
    CONNECT,          // Initiate a connection on a socket
    FALLOCATE,        // Preallocate or deallocate disk space within a file
    OPENAT,           // Open a file relative to a directory file descriptor
    CLOSE,            // Close a file descriptor
    FILES_UPDATE,     // Update registered files in the io_uring instance
    STATX,            // Get extended file statistics
    READ,             // Read from a file descriptor
    WRITE,            // Write to a file descriptor
    FADVISE,          // Offer advice about file access patterns
    MADVISE,          // Offer advice about memory management
    SEND,             // Send data on a socket
    RECV,             // Receive data from a socket
    OPENAT2,          // Extended version of OPENAT with additional options
    EPOLL_CTL,        // Control interface for an epoll file descriptor
    SPLICE,           // Move data between two file descriptors
    PROVIDE_BUFFERS,  // Provide buffers for asynchronous I/O
    REMOVE_BUFFERS,   // Remove previously provided buffers
    TEE,              // Duplicate pipe content
    SHUTDOWN,         // Shut down part of a full-duplex connection
    RENAMEAT,         // Rename a file
    UNLINKAT,         // Remove a file or directory
    MKDIRAT,          // Create a new directory
    SYMLINKAT,        // Create a symbolic link
    LINKAT,           // Create a hard link
    LAST              // Sentinel value indicating the end of the enumeration
}
