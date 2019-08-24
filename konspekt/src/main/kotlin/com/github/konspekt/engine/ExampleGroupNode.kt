package com.github.konspekt.engine

import com.github.konspekt.Example
import com.github.konspekt.ExampleGroup
import com.github.konspekt.Marker
import com.github.konspekt.Memoized
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestSource
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor
import org.junit.platform.engine.support.descriptor.FilePosition
import org.junit.platform.engine.support.descriptor.FileSource
import java.io.File

internal class ExampleGroupNode(
        id: UniqueId,
        name: String,
        source: TestSource,
        private val marker: Marker? = null,
        private val action: ExampleGroup.() -> Unit = {}
) : ExampleGroup, AbstractTestDescriptor(id, name, source) {

    companion object {
        val TYPE = TestDescriptor.Type.CONTAINER
    }

    override fun getType() = TYPE

    override fun exampleGroup(name: String, marker: Marker?, action: ExampleGroup.() -> Unit) {
        val id = uniqueId.childId(TYPE, name)

        appendChild(ExampleGroupNode(id, name, source(), marker.nested(this.marker), action).also {
            it.action.invoke(it)
        })
    }

    override fun example(name: String, marker: Marker?, action: Example.() -> Unit) {
        val id = uniqueId.childId(ExampleNode.TYPE, name)

        appendChild(ExampleNode(id, name, source(), marker.nested(this.marker), action))
    }

    private fun source(): TestSource {
        // Actions are lambdas so we are looking until we find one.
        val stackTrace = Thread.currentThread().stackTrace.find { it.methodName == "invoke" }

        return if (stackTrace == null) {
            return source.get()
        } else {
            val file = File(stackTrace.fileName)
            val filePosition = FilePosition.from(stackTrace.lineNumber)

            // IJ ignores file position for every source except the file one.
            // Reference: JUnit5TestExecutionListener#getLocationHintValue
            // https://youtrack.jetbrains.com/issue/IDEA-218420
            FileSource.from(file, filePosition)
        }
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
