package conspectus.engine

import conspectus.Example
import conspectus.ExampleGroup
import conspectus.Marker
import conspectus.Memoized
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestSource
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor

// IJ ignores file position for every source except the file one at this point.
// Reference: JUnit5TestExecutionListener#getLocationHintValue
// https://youtrack.jetbrains.com/issue/IDEA-218420
// Also Gradle hangs on class node sources with or without a file position.
// Reference: https://github.com/gradle/gradle/issues/5737
// For now it is fine to have a top-level source for class and none for nodes
// but it would be nice to resolve this in the future.

internal class ExampleGroupNode(
        id: UniqueId,
        name: String,
        source: TestSource?,
        private val marker: Marker? = null,
        private val action: ExampleGroup.() -> Unit = {}
) : ExampleGroup, AbstractTestDescriptor(id, name, source) {

    companion object {
        val TYPE = TestDescriptor.Type.CONTAINER
    }

    override fun getType() = TYPE

    override fun exampleGroup(name: String, marker: Marker?, action: ExampleGroup.() -> Unit) {
        val nodeId = uniqueId.childId(TYPE, name)
        val nodeMarker = marker.nested(this.marker)

        appendChild(ExampleGroupNode(nodeId, name, null, nodeMarker, action).also {
            it.action(it)
        })
    }

    override fun example(name: String, marker: Marker?, action: Example.() -> Unit) {
        val nodeId = uniqueId.childId(ExampleNode.TYPE, name)
        val nodeMarker = marker.nested(this.marker)

        appendChild(ExampleNode(nodeId, name, nodeMarker, action))
    }

    private fun appendChild(child: TestDescriptor) {
        check(children.none { it.displayName == child.displayName }) {
            "[${child.displayName}] repeating on the same hierarchy level. This is blocked to avoid collisions."
        }

        addChild(child)
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
        beforeEachStorage.forEach { it() }
    }

    fun executeAfterEach() {
        afterEachStorage.forEach { it() }

        memoizedStorage.forEach { it.reset() }
    }
}
