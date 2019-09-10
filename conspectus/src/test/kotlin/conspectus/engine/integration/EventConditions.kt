package conspectus.engine.integration

import org.assertj.core.api.Assertions
import org.junit.platform.testkit.engine.EventConditions.*
import org.junit.platform.testkit.engine.EventType

fun namedContainer(displayName: String) = Assertions.allOf(container(), displayName(displayName))
fun namedTest(displayName: String) = Assertions.allOf(test(), displayName(displayName))

fun skipped() = Assertions.allOf(type(EventType.SKIPPED))