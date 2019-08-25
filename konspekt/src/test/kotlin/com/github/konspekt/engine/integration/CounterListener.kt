package com.github.konspekt.engine.integration

import com.github.konspekt.SpecListener
import java.util.concurrent.atomic.AtomicInteger

class CounterListener : SpecListener {

    companion object {
        val COUNTER_BEFORE_EACH = AtomicInteger()
        val COUNTER_AFTER_EACH = AtomicInteger()

        fun resetCounters() {
            COUNTER_BEFORE_EACH.set(0)
            COUNTER_AFTER_EACH.set(0)
        }
    }

    override fun onBeforeEach() {
        COUNTER_BEFORE_EACH.incrementAndGet()
    }

    override fun onAfterEach() {
        COUNTER_AFTER_EACH.incrementAndGet()
    }
}