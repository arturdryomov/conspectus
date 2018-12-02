package com.github.konspekt.samples

import java.util.concurrent.atomic.AtomicInteger

interface Counter {

    fun increment()
    fun decrement()

    fun current(): Int

    class Impl : Counter {

        private val current = AtomicInteger(0)

        override fun increment() {
            current.incrementAndGet()
        }

        override fun decrement() {
            current.decrementAndGet()
        }

        override fun current() = current.get()
    }
}