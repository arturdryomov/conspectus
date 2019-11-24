package conspectus

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIllegalStateException
import org.mockito.Mockito.*

class MemoizedSpec : Spec({

    val env by memoized { Environment() }

    it("throws on accessing") {
        assertThatIllegalStateException().isThrownBy { env.value }
    }

    it("does not call creator") {
        verifyNoInteractions(env.creator)
    }

    context("set up") {

        beforeEach {
            env.valueContainer.setUp()
        }

        it("does not call creator") {
            verifyNoInteractions(env.creator)
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
                    verifyNoInteractions(env.creator)
                }

                it("returns creator result") {
                    assertThat(env.value).isEqualTo(Environment.CREATOR_RESULT)
                }
            }

            context("tear down") {

                beforeEach {
                    clearInvocations(env.creator)

                    env.valueContainer.tearDown()
                }

                it("throws on accessing") {
                    assertThatIllegalStateException().isThrownBy { env.value }
                }

                it("does not call creator") {
                    verifyNoInteractions(env.creator)
                }

                context("set up") {

                    beforeEach {
                        env.valueContainer.setUp()
                    }

                    it("does not call creator") {
                        verifyNoInteractions(env.creator)
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
                    }
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