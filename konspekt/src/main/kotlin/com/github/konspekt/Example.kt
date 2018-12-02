package com.github.konspekt

import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor
import org.junit.platform.engine.support.hierarchical.Node

@Dsl
interface Example

class ExampleNode(
        id: UniqueId,
        name: String,
        private val action: Example.() -> Unit
) : Example, Node<EngineExecutionContext>, AbstractTestDescriptor(id, name) {

    companion object {
        val TYPE = TestDescriptor.Type.TEST
    }

    private object CachedExample : Example

    override fun getType() = TYPE

    override fun before(context: EngineExecutionContext?): EngineExecutionContext {
        group().executeBeforeEach()

        return super.before(context)
    }

    override fun execute(context: EngineExecutionContext, dynamicTestExecutor: Node.DynamicTestExecutor): EngineExecutionContext {
        action.invoke(CachedExample)

        return super.execute(context, dynamicTestExecutor)
    }

    override fun after(context: EngineExecutionContext?) {
        super.after(context)

        group().executeAfterEach()
    }

    private fun group() = parent.get() as ExampleGroupNode
}