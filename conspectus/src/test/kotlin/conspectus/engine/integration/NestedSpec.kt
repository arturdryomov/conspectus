package conspectus.engine.integration

import conspectus.Spec
import org.junit.platform.testkit.engine.EventConditions.*

class NestedSpec : Spec({

    it("success @ root") {
    }

    it("failure @ root") {
        throw AssertionError()
    }

    describe("1st level") {

        it("success @ 1st level") {
        }

        it("failure @ 1st level") {
            throw AssertionError()
        }

        context("2nd level") {

            it("success @ 2nd level") {
            }
        }

        describe("2nd level") {

            it("success @ 2nd level") {
            }
        }
    }

}) {

    companion object {
        val CLASS = NestedSpec::class.java

        // @formatter:off
        val EVENTS = arrayOf(
            event(namedContainer(CLASS.simpleName), started()),
                event(namedTest("it success @ root"), started()),
                event(namedTest("it success @ root"), finishedSuccessfully()),

                event(namedTest("it failure @ root"), started()),
                event(namedTest("it failure @ root"), finishedWithFailure()),

                event(namedContainer("describe 1st level"), started()),
                    event(namedTest("it success @ 1st level"), started()),
                    event(namedTest("it success @ 1st level"), finishedSuccessfully()),

                    event(namedTest("it failure @ 1st level"), started()),
                    event(namedTest("it failure @ 1st level"), finishedWithFailure()),

                    event(namedContainer("context 2nd level"), started()),
                        event(namedTest("it success @ 2nd level"), started()),
                        event(namedTest("it success @ 2nd level"), finishedSuccessfully()),
                    event(namedContainer("context 2nd level"), finishedSuccessfully()),

                    event(namedContainer("describe 2nd level"), started()),
                        event(namedTest("it success @ 2nd level"), started()),
                        event(namedTest("it success @ 2nd level"), finishedSuccessfully()),
                    event(namedContainer("describe 2nd level"), finishedSuccessfully()),
                event(namedContainer("describe 1st level"), finishedSuccessfully()),
            event(namedContainer(CLASS.simpleName), finishedSuccessfully())
        )
        // @formatter:on
    }

}