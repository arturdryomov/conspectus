package com.github.konspekt.samples

import java.util.concurrent.atomic.AtomicInteger

interface Counter {

    fun increment()
    fun decrement()

    fun current(): Int

    class Impl(private val analytics: Analytics) : Counter {

        private val current = AtomicInteger(0)

        override fun increment() {
            current.incrementAndGet()
            analytics.trackEvent(Analytics.Event.CounterIncrement)
        }

        override fun decrement() {
            current.decrementAndGet()
            analytics.trackEvent(Analytics.Event.CounterDecrement)
        }

        override fun current() = current.get()
    }
}