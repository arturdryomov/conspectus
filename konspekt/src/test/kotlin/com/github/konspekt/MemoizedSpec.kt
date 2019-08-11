package com.github.konspekt

import org.assertj.core.api.Assertions.assertThat
import org.mockito.Mockito.*

class MemoizedSpec : Spec({

    val env by memoized { Environment() }

    it("does not call creator") {
        verifyZeroInteractions(env.creator)
    }

    context("access") {

        beforeEach {
            env.value
        }

        it("calls creator") {
            verifyOnly(env.creator).invoke()
        }

        it("returns creator result") {
            assertThat(env.value).isEqualTo(Environment.CREATOR_RESULT)
        }

        context("access again") {

            beforeEach {
                clearInvocations(env.creator)

                env.value
            }

            it("does not call creator") {
                verifyZeroInteractions(env.creator)
            }
        }

        context("reset") {

            beforeEach {
                env.valueContainer.reset()
            }

            context("access") {

                beforeEach {
                    clearInvocations(env.creator)

                    env.value
                }

                it("calls creator") {
                    verifyOnly(env.creator).invoke()
                }
            }
        }
    }

}) {

    class Environment {
        companion object {
            const val CREATOR_RESULT = "value"
        }

        val creator = mock<() -> String> {
            `when`(invoke()).thenReturn(CREATOR_RESULT)
        }

        val valueContainer = Memoized(creator)
        val value by valueContainer
    }

}