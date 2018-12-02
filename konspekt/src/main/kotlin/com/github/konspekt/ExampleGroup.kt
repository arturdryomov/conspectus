package com.github.konspekt

import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestSource
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor
import org.junit.platform.engine.support.hierarchical.Node

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
        private val action: ExampleGroup.() -> Unit = {},
        source: TestSource? = null
) : ExampleGroup, Node<EngineExecutionContext>, AbstractTestDescriptor(id, name, source) {

    companion object {
        val TYPE = TestDescriptor.Type.CONTAINER
    }

    override fun getType() = TYPE

    override fun example(name: String, action: Example.() -> Unit) {
        val child = ExampleNode(uniqueId.childId(ExampleNode.TYPE, name), name, action)

        addChild(child)
    }

    override fun exampleGroup(name: String, action: ExampleGroup.() -> Unit) {
        val child = ExampleGroupNode(uniqueId.childId(TYPE, name), name, action).also {
            it.action.invoke(it)
        }

        addChild(child)
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
