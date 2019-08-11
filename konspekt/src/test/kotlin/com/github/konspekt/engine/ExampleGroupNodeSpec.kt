package com.github.konspekt.engine

import com.github.konspekt.Spec
import com.github.konspekt.mock
import com.github.konspekt.verifyOnly
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIllegalStateException
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.support.descriptor.ClassSource

class ExampleGroupNodeSpec : Spec({

    val env by memoized { Environment() }

    it("returns declared type") {
        assertThat(env.node.type).isEqualTo(ExampleGroupNode.TYPE)
    }

    context("declare example") {

        val name = "example"

        beforeEach {
            env.node.example(name) {}
        }

        it("adds child") {
            assertThat(env.node.children)
                    .hasSize(1)
                    .first().matches { it.displayName == name && it.isTest }
        }

        it("throws on declaring same named example") {
            assertThatIllegalStateException().isThrownBy { env.node.example(name) {} }
        }
    }

    context("declare example group") {

        val name = "example group"

        beforeEach {
            env.node.exampleGroup(name) {}
        }

        it("adds child") {
            assertThat(env.node.children)
                    .hasSize(1)
                    .first().matches { it.displayName == name && it.isContainer }
        }

        it("throws on declaring same named example group") {
            assertThatIllegalStateException().isThrownBy { env.node.exampleGroup(name) {} }
        }
    }

    context("execute before each") {

        beforeEach {
            env.node.executeBeforeEach()
        }

        it("calls before each actions") {
            verifyOnly(env.nodeBeforeEach).invoke()
        }
    }

    context("execute after each") {

        beforeEach {
            env.node.executeAfterEach()
        }

        it("calls after each actions") {
            verifyOnly(env.nodeAfterEach).invoke()
        }
    }

}) {

    class Environment {
        companion object {
            private val ID = UniqueId.root("group node", "group node")
            private val SOURCE = ClassSource.from(ExampleGroupNodeSpec::class.java)
        }

        val nodeBeforeEach = mock<() -> Unit>()
        val nodeAfterEach = mock<() -> Unit>()

        internal val node = (ExampleGroupNode(ID, "Group Node", SOURCE) {}).apply {
            beforeEach(nodeBeforeEach)
            afterEach(nodeAfterEach)
        }
    }

}