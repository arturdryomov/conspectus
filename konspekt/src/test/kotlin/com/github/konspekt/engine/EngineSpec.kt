package com.github.konspekt.engine

import com.github.konspekt.Spec
import com.github.konspekt.engine.suite.PrimarySpec
import com.github.konspekt.engine.suite.SecondarySpec
import org.assertj.core.api.Assertions
import org.assertj.core.api.Condition
import org.junit.platform.engine.discovery.ClassNameFilter
import org.junit.platform.engine.discovery.DiscoverySelectors
import org.junit.platform.engine.discovery.PackageNameFilter
import org.junit.platform.testkit.engine.EngineExecutionResults
import org.junit.platform.testkit.engine.EngineTestKit
import org.junit.platform.testkit.engine.Event
import org.junit.platform.testkit.engine.EventConditions.*

class EngineSpec : Spec({

    val engineId = "konspekt"

    val classPrimarySpec = PrimarySpec::class.java
    val classSecondarySpec = SecondarySpec::class.java

    val packageNameSpecs = PrimarySpec::class.java.`package`.name

    fun namedContainer(displayName: String) = Assertions.allOf(container(), displayName(displayName))
    fun namedTest(displayName: String) = Assertions.allOf(test(), displayName(displayName))

    fun EngineExecutionResults.assertEvents(vararg events: Array<Condition<Event>>) {
        val engineEvents = arrayOf(
                event(engine(), started()),
                *(events.flatten().toTypedArray()),
                event(engine(), finishedSuccessfully())
        )

        all().assertEventsMatchExactly(*engineEvents)
    }

    val eventsPrimarySpec = arrayOf(
            // @formatter:off
            event(namedContainer(classPrimarySpec.simpleName), started()),
                event(namedTest("it works in root"), started()),
                event(namedTest("it works in root"), finishedSuccessfully()),

                event(namedTest("it fails in root"), started()),
                event(namedTest("it fails in root"), finishedWithFailure()),

                event(namedContainer("describe 1st level"), started()),
                    event(namedTest("it works in 1st level describe"), started()),
                    event(namedTest("it works in 1st level describe"), finishedSuccessfully()),

                    event(namedTest("it fails in 1st level describe"), started()),
                    event(namedTest("it fails in 1st level describe"), finishedWithFailure()),

                    event(namedContainer("context 2nd level"), started()),
                        event(namedTest("it works in 2nd level context"), started()),
                        event(namedTest("it works in 2nd level context"), finishedSuccessfully()),
                    event(namedContainer("context 2nd level"), finishedSuccessfully()),

                    event(namedContainer("describe 2nd level"), started()),
                        event(namedTest("it works in 2nd level describe"), started()),
                        event(namedTest("it works in 2nd level describe"), finishedSuccessfully()),
                    event(namedContainer("describe 2nd level"), finishedSuccessfully()),
                event(namedContainer("describe 1st level"), finishedSuccessfully()),
            event(namedContainer(classPrimarySpec.simpleName), finishedSuccessfully())
            // @formatter:on
    )

    val eventsSecondarySpec = arrayOf(
            event(namedContainer(classSecondarySpec.simpleName), started()),
            event(namedTest("it works in root"), started()),
            event(namedTest("it works in root"), finishedSuccessfully()),
            event(namedContainer(classSecondarySpec.simpleName), finishedSuccessfully())
    )

    context("discover primary spec as class") {

        it("results in primary spec events") {
            EngineTestKit
                    .engine(engineId)
                    .selectors(DiscoverySelectors.selectClass(classPrimarySpec))
                    .execute()
                    .assertEvents(eventsPrimarySpec)
        }
    }

    context("discover secondary spec as class") {

        it("results in secondary spec events") {
            EngineTestKit
                    .engine(engineId)
                    .selectors(DiscoverySelectors.selectClass(classSecondarySpec))
                    .execute()
                    .assertEvents(eventsSecondarySpec)
        }
    }

    context("discover specs as package") {

        it("results in both primary and secondary spec events") {
            EngineTestKit
                    .engine(engineId)
                    .selectors(DiscoverySelectors.selectPackage(packageNameSpecs))
                    .execute()
                    .assertEvents(eventsPrimarySpec, eventsSecondarySpec)
        }
    }

    context("discover specs as package, excluding package") {

        it("results in blank events") {
            EngineTestKit
                    .engine(engineId)
                    .selectors(DiscoverySelectors.selectPackage(packageNameSpecs))
                    .filters(PackageNameFilter.excludePackageNames(packageNameSpecs))
                    .execute()
                    .assertEvents()
        }
    }

    context("discover specs as package, excluding secondary spec") {

        it("results in primary spec events") {
            EngineTestKit
                    .engine(engineId)
                    .selectors(DiscoverySelectors.selectPackage(packageNameSpecs))
                    .filters(ClassNameFilter.excludeClassNamePatterns(".*${classSecondarySpec.simpleName}.*"))
                    .execute()
                    .assertEvents(eventsPrimarySpec)
        }
    }

})