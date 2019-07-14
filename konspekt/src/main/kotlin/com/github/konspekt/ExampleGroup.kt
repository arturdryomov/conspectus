package com.github.konspekt

import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestSource
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor
import org.junit.platform.engine.support.descriptor.FilePosition
import org.junit.platform.engine.support.descriptor.FileSource
import org.junit.platform.engine.support.hierarchical.Node
import java.io.File

@Dsl
interface ExampleGroup {
    fun <T : Any> memoized(creator: () -> T): Memoized<T>

    fun example(name: String, action: Example.() -> Unit)
    fun exampleGroup(name: String, action: ExampleGroup.() -> Unit)

    fun beforeEach(action: () -> Unit)
    fun afterEach(action: () -> Unit)
}

class ExampleGroupNode(
        id: UniqueId,
        name: String,
        source: TestSource,
        private val action: ExampleGroup.() -> Unit = {}
) : ExampleGroup, Node<EngineExecutionContext>, AbstractTestDescriptor(id, name, source) {

    companion object {
        val TYPE = TestDescriptor.Type.CONTAINER
    }

    override fun getType() = TYPE

    override fun example(name: String, action: Example.() -> Unit) {
        val child = ExampleNode(uniqueId.childId(ExampleNode.TYPE, name), name, source(), action)

        appendChild(child)
    }

    override fun exampleGroup(name: String, action: ExampleGroup.() -> Unit) {
        val child = ExampleGroupNode(uniqueId.childId(TYPE, name), name, source(), action).also {
            it.action.invoke(it)
        }

        appendChild(child)
    }

    private fun appendChild(child: TestDescriptor) {
        if (children.any { it.uniqueId == child.uniqueId }) {
            val kind = when (child.type) {
                TestDescriptor.Type.CONTAINER -> "Example group"
                else -> "Example"
            }

            throw IllegalStateException("$kind [${child.displayName}] already exists on the same hierarchy level. This is blocked to avoid possible confusion.")
        } else {
            addChild(child)
        }
    }

    private fun source(): TestSource {
        // 0: Thread#stackTrace
        // 1: ExampleGroup#source
        // 2: ExampleGroup#exampleGroup or ExampleGroup#example
        // 3: DSL invocation
        val stackTrace = Thread.currentThread().stackTrace[3]

        val file = File(stackTrace.fileName)
        val filePosition = FilePosition.from(stackTrace.lineNumber)

        // IJ ignores file position for every source except the file one.
        // Reference: JUnit5TestExecutionListener#getLocationHintValue
        return FileSource.from(file, filePosition)
    }

    private val beforeEachActions = mutableSetOf<() -> Unit>()
    private val afterEachActions = mutableSetOf<() -> Unit>()

    override fun beforeEach(action: () -> Unit) {
        beforeEachActions.add(action)
    }

    override fun afterEach(action: () -> Unit) {
        afterEachActions.add(action)
    }

    fun executeBeforeEach() {
        beforeEachActions.forEach { it.invoke() }
    }

    fun executeAfterEach() {
        afterEachActions.forEach { it.invoke() }

        memoizedStorage.forEach { it.reset() }
    }

    private val memoizedStorage = mutableSetOf<Memoized<Any>>()

    override fun <T : Any> memoized(creator: () -> T): Memoized<T> {
        val result = Memoized(creator)

        memoizedStorage.add(result)

        return result
    }
}
