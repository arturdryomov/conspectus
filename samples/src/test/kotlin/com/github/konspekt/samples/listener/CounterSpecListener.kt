package com.github.konspekt.samples.listener

import com.github.konspekt.SpecListener
import java.util.concurrent.atomic.AtomicInteger

class CounterSpecListener : SpecListener {

    private val counter = AtomicInteger()

    override fun onBeforeEach() {
        println(":: Current count is [${counter.incrementAndGet()}]")
    }
}