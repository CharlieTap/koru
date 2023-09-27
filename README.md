## Kotlin Native liburing bindings

---

This repository provides 1:1 bindings with the liburing api, abstracting away the pointers, structs and ffi interactions and replacing them with a kotlin friendly interface.
The library is designed to provide a playground for liburing, for most liburing api calls you will find a kotlin function equivalent, for example:

Given the liburing call

```c
io_uring_queue_init()
```
You'll find both a functional interface to program against and a function implementing the said interface.

```kotlin

fun interface InitRing: (RingEntries, RingConfiguration?) -> InitRingResult

fun initRing(
entries: RingEntries,
configuration: RingConfiguration? = null,
)
```

Note

This library is not intended for production use.
Whilst iouring/liburing provides the most performant io interface for linux, using these bindings will likely result in suboptimal performance.
Kotlin is not a systems language, the overhead of managing the ring, queues and their entries inside of Kotlin is expensive.
