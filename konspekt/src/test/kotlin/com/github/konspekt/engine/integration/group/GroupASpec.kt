package com.github.konspekt.engine.integration.group

import com.github.konspekt.Spec
import com.github.konspekt.engine.integration.namedContainer
import com.github.konspekt.engine.integration.namedTest
import org.junit.platform.testkit.engine.EventConditions.*

class GroupASpec : Spec({

    it("works") {
    }

}) {

    companion object {
        val CLASS = GroupASpec::class.java

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