package com.github.konspekt.engine.integration

import com.github.konspekt.Spec
import org.junit.platform.testkit.engine.EventConditions.*

class MarkedSpec : Spec({

    fit("success @ root") {
    }

    it("skip @ root") {
    }

    fdescribe("1st level") {

        it("success @ 1st level") {
        }

        xit("skip @ 1st level") {
        }

        context("2nd level") {

            it("success @ 2nd level") {
            }
        }

        xdescribe("2nd level") {

            it("skip @ 2nd level") {
            }
        }
    }

}) {

    companion object {
        val CLASS = MarkedSpec::class.java

        // @formatter:off
        val EVENTS = arrayOf(
            event(namedContainer(CLASS.simpleName), started()),
                event(namedTest("fit success @ root"), started()),
                event(namedTest("fit success @ root"), finishedSuccessfully()),

                event(namedTest("it skip @ root"), skipped()),

                event(namedContainer("fdescribe 1st level"), started()),
                    event(namedTest("it success @ 1st level"), started()),
                    event(namedTest("it success @ 1st level"), finishedSuccessfully()),

                    event(namedTest("xit skip @ 1st level"), skipped()),

                    event(namedContainer("context 2nd level"), started()),
                        event(namedTest("it success @ 2nd level"), started()),
                        event(namedTest("it success @ 2nd level"), finishedSuccessfully()),
                    event(namedContainer("context 2nd level"), finishedSuccessfully()),

                    event(namedContainer("xdescribe 2nd level"), started()),
                        event(namedTest("it skip @ 2nd level"), skipped()),
                    event(namedContainer("xdescribe 2nd level"), finishedSuccessfully()),
                event(namedContainer("fdescribe 1st level"), finishedSuccessfully()),
            event(namedContainer(CLASS.simpleName), finishedSuccessfully())
        )
        // @formatter:on
    }

}