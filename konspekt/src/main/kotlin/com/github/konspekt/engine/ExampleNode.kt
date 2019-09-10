package com.github.konspekt.engine

import com.github.konspekt.Example
import com.github.konspekt.Marker
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestSource
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor
import org.junit.platform.engine.support.hierarchical.Node

internal class ExampleNode(
        id: UniqueId,
        name: String,
        source: TestSource?,
        private val marker: Marker? = null,
        private val action: Example.() -> Unit
) : Example, Markable, Node<EngineExecutionContext>, AbstractTestDescriptor(id, name, source) {

    companion object {
        val TYPE = TestDescriptor.Type.TEST

        private object BlankExample : Example
    }

    private val parentGroups by lazy { parentGroups() }

    override fun getType() = TYPE

    override fun marked(marker: Marker) = marker == this.marker

    override fun shouldBeSkipped(context: EngineExecutionContext) = marker.toSkipResult(context)

    override fun before(context: EngineExecutionContext): EngineExecutionContext {
        parentGroups.reversed().forEach { it.executeBeforeEach() }

        return context
    }

    override fun execute(context: EngineExecutionContext, dynamicTestExecutor: Node.DynamicTestExecutor): EngineExecutionContext {
        action.invoke(BlankExample)

        return context
    }

    override fun after(context: EngineExecutionContext) {
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