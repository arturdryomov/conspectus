package conspectus.engine.integration.group

import conspectus.Spec
import conspectus.engine.integration.namedContainer
import conspectus.engine.integration.namedTest
import org.junit.platform.testkit.engine.EventConditions.*

class GroupBSpec : Spec({

    it("works") {
    }

}) {

    companion object {
        val CLASS = GroupBSpec::class.java

        // @formatter:off
        val EVENTS = arrayOf(
            event(namedContainer(CLASS.simpleName), started()),
                event(namedTest("it works"), started()),
                event(namedTest("it works"), finishedSuccessfully()),
            event(namedContainer(CLASS.simpleName), finishedSuccessfully())
        )
        // @formatter:on
    }

}