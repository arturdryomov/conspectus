package conspectus.engine

import conspectus.Spec
import conspectus.engine.integration.MarkedSpec
import conspectus.engine.integration.NestedSpec
import conspectus.engine.integration.group.Group
import conspectus.engine.integration.group.GroupASpec
import conspectus.engine.integration.group.GroupBSpec
import conspectus.engine.integration.group.GroupCSpec
import org.assertj.core.api.Condition
import org.junit.platform.engine.discovery.ClassNameFilter
import org.junit.platform.engine.discovery.DiscoverySelectors
import org.junit.platform.engine.discovery.PackageNameFilter
import org.junit.platform.testkit.engine.EngineExecutionResults
import org.junit.platform.testkit.engine.EngineTestKit
import org.junit.platform.testkit.engine.Event
import org.junit.platform.testkit.engine.EventConditions.*

class IntegrationSpec : Spec({

    val engineId = "conspectus"

    fun EngineExecutionResults.assertEvents(vararg events: Array<Condition<Event>>) {
        val engineEvents = arrayOf(
                event(engine(), started()),
                *(events.flatten().toTypedArray()),
                event(engine(), finishedSuccessfully())
        )

        allEvents().assertEventsMatchExactly(*engineEvents)
    }

    mapOf(MarkedSpec.CLASS to MarkedSpec.EVENTS, NestedSpec.CLASS to NestedSpec.EVENTS).forEach { (specClass, specEvents) ->

        it("executes [${specClass.simpleName}] class") {
            EngineTestKit
                    .engine(engineId)
                    .selectors(DiscoverySelectors.selectClass(specClass))
                    .execute()
                    .assertEvents(specEvents)
        }
    }

    describe("group") {

        // The discovery order is almost random so expect issues on this front.

        it("executes everything on discovering package") {
            EngineTestKit
                    .engine(engineId)
                    .selectors(DiscoverySelectors.selectPackage(Group.PACKAGE))
                    .execute()
                    .assertEvents(GroupASpec.EVENTS, GroupBSpec.EVENTS, GroupCSpec.EVENTS)
        }

        it("executes nothing on discovering package and filtering it out") {
            EngineTestKit
                    .engine(engineId)
                    .selectors(DiscoverySelectors.selectPackage(Group.PACKAGE))
                    .filters(PackageNameFilter.excludePackageNames(Group.PACKAGE))
                    .execute()
                    .assertEvents()
        }

        it("executes everything except filtered out class on discovering package") {
            EngineTestKit
                    .engine(engineId)
                    .selectors(DiscoverySelectors.selectPackage(Group.PACKAGE))
                    .filters(ClassNameFilter.excludeClassNamePatterns(".*${GroupASpec.CLASS.simpleName}.*"))
                    .execute()
                    .assertEvents(GroupBSpec.EVENTS, GroupCSpec.EVENTS)
        }
    }
})