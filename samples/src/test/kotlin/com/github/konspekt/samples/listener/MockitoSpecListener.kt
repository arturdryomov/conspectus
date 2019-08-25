package com.github.konspekt.samples.listener

import com.github.konspekt.SpecListener
import org.mockito.Mockito

class MockitoSpecListener : SpecListener {

    override fun onAfterEach() {
        Mockito.framework().clearInlineMocks()
    }
}