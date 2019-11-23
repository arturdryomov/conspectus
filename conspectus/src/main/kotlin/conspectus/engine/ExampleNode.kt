package conspectus.engine

import conspectus.Example
import conspectus.Marker
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor
import org.junit.platform.engine.support.hierarchical.Node

internal class ExampleNode(
        id: UniqueId,
        name: String,
        override val marker: Marker? = null,
        private val action: Example.() -> Unit
) : Example, Markable, Node<EngineExecutionContext>, AbstractTestDescriptor(id, name) {

    companion object {
        val TYPE = TestDescriptor.Type.TEST

        private object BlankExample : Example
    }

    private val parentGroups by lazy { parentGroups() }

    private fun TestDescriptor.parentGroups(): List<ExampleGroupNode> {
        val parentNode: TestDescriptor? = parent.orElse(null)

        return if (parentNode is ExampleGroupNode) {
            parentNode.parentGroups() + parentNode
        } else {
            emptyList()
        }
    }

    override fun getType() = TYPE

    override fun shouldBeSkipped(context: EngineExecutionContext) = marker.toSkipResult(context)

    override fun before(context: EngineExecutionContext): EngineExecutionContext {
        parentGroups.forEach { it.executeBeforeEach() }

        return context
    }

    override fun execute(context: EngineExecutionContext, dynamicTestExecutor: Node.DynamicTestExecutor): EngineExecutionContext {
        action.invoke(BlankExample)

        return context
    }

    override fun after(context: EngineExecutionContext) {
        parentGroups.reversed().forEach { it.executeAfterEach() }
    }
}