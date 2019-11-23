package conspectus.engine

import conspectus.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.platform.engine.UniqueId
import org.mockito.Mockito

class ExampleNodeSpec : Spec({

    val env by memoized { Environment() }

    val context = EngineExecutionContext(markersAvailable = emptySet())

    it("returns declared type") {
        assertThat(env.node.type).isEqualTo(ExampleNode.TYPE)
    }

    context("before") {

        beforeEach {
            env.node.before(context)
        }

        it("calls grandparent before each, then parent before each") {
            val order = Mockito.inOrder(env.nodeGrandParentBeforeEach, env.nodeParentBeforeEach)

            order.verify(env.nodeGrandParentBeforeEach).invoke()
            order.verify(env.nodeParentBeforeEach).invoke()
            order.verifyNoMoreInteractions()
        }
    }

    context("execute") {

        beforeEach {
            env.node.execute(context, mock())
        }

        it("calls action") {
            verifyOnly(env.nodeAction).invoke(anything())
        }
    }

    context("after") {

        beforeEach {
            env.node.after(context)
        }

        it("calls parent after each, then grandparent after each") {
            val order = Mockito.inOrder(env.nodeGrandParentAfterEach, env.nodeParentAfterEach)

            order.verify(env.nodeParentAfterEach).invoke()
            order.verify(env.nodeGrandParentAfterEach).invoke()
            order.verifyNoMoreInteractions()
        }
    }

}) {

    class Environment {

        companion object {
            private val ID = UniqueId.root("node", "node")
        }

        val nodeGrandParentBeforeEach = mock<() -> Unit>()
        val nodeGrandParentAfterEach = mock<() -> Unit>()

        private val nodeGrandParent = (ExampleGroupNode(ID, "Node grandparent", null) {}).apply {
            beforeEach(nodeGrandParentBeforeEach)
            afterEach(nodeGrandParentAfterEach)
        }

        val nodeParentBeforeEach = mock<() -> Unit>()
        val nodeParentAfterEach = mock<() -> Unit>()

        private val nodeParent = (ExampleGroupNode(ID, "Node parent", null) {}).apply {
            beforeEach(nodeParentBeforeEach)
            afterEach(nodeParentAfterEach)
        }

        val nodeAction = mock<Example.() -> Unit>()
        internal val node = ExampleNode(ID, "Node", null, nodeAction)

        init {
            nodeParent.setParent(nodeGrandParent)
            node.setParent(nodeParent)
        }
    }

}