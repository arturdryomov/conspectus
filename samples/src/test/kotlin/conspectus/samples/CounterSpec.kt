package conspectus.samples

import conspectus.Spec
import org.assertj.core.api.Assertions.assertThat
import org.mockito.Mockito.*

class CounterSpec : Spec({

    val env by memoized { Environment() }

    it("has value of 0") {
        assertThat(env.counter.current()).isZero()
    }

    it("does not track analytics event") {
        verifyZeroInteractions(env.analytics)
    }

    context("increment") {

        beforeEach {
            env.counter.increment()
        }

        it("changes value to 1") {
            assertThat(env.counter.current()).isEqualTo(1)
        }

        it("tracks analytics event") {
            verifyOnly(env.analytics).trackEvent(Analytics.Event.CounterIncrement)
        }

        context("decrement") {

            beforeEach {
                clearInvocations(env.analytics)

                env.counter.decrement()
            }

            it("changes value to 0") {
                assertThat(env.counter.current()).isZero()
            }

            it("tracks analytics event") {
                verifyOnly(env.analytics).trackEvent(Analytics.Event.CounterDecrement)
            }
        }
    }

    context("decrement") {

        beforeEach {
            env.counter.decrement()
        }

        it("changes value to -1") {
            assertThat(env.counter.current()).isEqualTo(-1)
        }

        it("tracks analytics event") {
            verifyOnly(env.analytics).trackEvent(Analytics.Event.CounterDecrement)
        }

        context("increment") {

            beforeEach {
                clearInvocations(env.analytics)

                env.counter.increment()
            }

            it("changes value to 0") {
                assertThat(env.counter.current()).isZero()
            }

            it("tracks analytics event") {
                verifyOnly(env.analytics).trackEvent(Analytics.Event.CounterIncrement)
            }
        }
    }

}) {

    class Environment {
        val analytics = mock(Analytics::class.java)

        val counter = Counter.Impl(analytics)
    }

}
