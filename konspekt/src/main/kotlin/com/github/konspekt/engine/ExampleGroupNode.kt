package com.github.konspekt.engine

import com.github.konspekt.Example
import com.github.konspekt.ExampleGroup
import com.github.konspekt.Memoized
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestSource
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor
import org.junit.platform.engine.support.descriptor.FilePosition
import org.junit.platform.engine.support.descriptor.FileSource
import org.junit.platform.engine.support.hierarchical.Node
import java.io.File

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

    override fun exampleGroup(name: String, action: ExampleGroup.() -> Unit) {
        val child = ExampleGroupNode(uniqueId.childId(TYPE, name), name, source(), action).also {
            it.action.invoke(it)
        }

        appendChild(child)
    }

    override fun example(name: String, action: Example.() -> Unit) {
        val child = ExampleNode(uniqueId.childId(ExampleNode.TYPE, name), name, source(), action)

        appendChild(child)
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

    private fun appendChild(child: TestDescriptor) {
        if (children.any { it.displayName == child.displayName }) {
            throw IllegalStateException("[${child.displayName}] repeating on the same hierarchy level. This is blocked to avoid possible confusion.")
        } else {
            addChild(child)
        }
    }

    private val beforeEachStorage = mutableSetOf<() -> Unit>()
    private val afterEachStorage = mutableSetOf<() -> Unit>()

    private val memoizedStorage = mutableSetOf<Memoized<Any>>()

    override fun beforeEach(action: () -> Unit) {
        beforeEachStorage.add(action)
    }

    override fun afterEach(action: () -> Unit) {
        afterEachStorage.add(action)
    }

    override fun <T : Any> memoized(creator: () -> T): Memoized<T> = Memoized(creator).apply {
        memoizedStorage.add(this)
    }

    fun executeBeforeEach() {
        beforeEachStorage.forEach { it.invoke() }
    }

    fun executeAfterEach() {
        afterEachStorage.forEach { it.invoke() }

        memoizedStorage.forEach { it.reset() }
    }
}