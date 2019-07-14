package com.github.konspekt

import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestSource
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor
import org.junit.platform.engine.support.hierarchical.Node

@Dsl
interface Example

class ExampleNode(
        id: UniqueId,
        name: String,
        source: TestSource,
        private val action: Example.() -> Unit
) : Example, Node<EngineExecutionContext>, AbstractTestDescriptor(id, name, source) {

    companion object {
        val TYPE = TestDescriptor.Type.TEST

        private object BlankExample : Example
    }

    private val parentGroups by lazy { parentGroups() }

    override fun getType() = TYPE

    override fun before(context: EngineExecutionContext?): EngineExecutionContext {
        parentGroups.reversed().forEach { it.executeBeforeEach() }

        return super.before(context)
    }

    override fun execute(context: EngineExecutionContext, dynamicTestExecutor: Node.DynamicTestExecutor): EngineExecutionContext {
        action.invoke(BlankExample)

        return super.execute(context, dynamicTestExecutor)
    }

    override fun after(context: EngineExecutionContext?) {
        super.after(context)

        parentGroups.forEach { it.executeAfterEach() }
    }

    private fun parentGroups(node: TestDescriptor = this): List<ExampleGroupNode> {
        val parentNode = if (node.parent.isPresent) {
            node.parent.get()
        } else {
            null
        }

        return if (parentNode is ExampleGroupNode) {
            listOf(parentNode) + parentGroups(parentNode)
        } else {
            emptyList()
        }
    }
}