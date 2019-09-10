package com.github.konspekt.samples

import org.junit.platform.engine.TestExecutionResult
import org.junit.platform.launcher.TestExecutionListener
import org.junit.platform.launcher.TestIdentifier
import org.mockito.Mockito

class MockitoListener : TestExecutionListener {

    override fun executionFinished(testIdentifier: TestIdentifier, testExecutionResult: TestExecutionResult) {
        Mockito.framework().clearInlineMocks()
    }
}