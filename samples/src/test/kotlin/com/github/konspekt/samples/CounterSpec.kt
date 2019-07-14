package com.github.konspekt.samples

import com.github.konspekt.Spec
import org.assertj.core.api.Assertions.assertThat
import org.junit.platform.commons.annotation.Testable
import org.mockito.Mockito.*

@Testable
class CounterSpec : Spec({

    val env by memoized { Environment() }

    example("value is 0") {
        assertThat(env.counter.current()).isZero()
    }

    example("does not track analytics event") {
        verifyZeroInteractions(env.analytics)
    }

    exampleGroup("increment") {

        beforeEach {
            env.counter.increment()
        }

        example("changes value to 1") {
            assertThat(env.counter.current()).isEqualTo(1)
        }

        example("tracks analytics event") {
            verifyOnly(env.analytics).trackEvent(Analytics.Event.CounterIncrement)
        }

        exampleGroup("decrement") {

            beforeEach {
                clearInvocations(env.analytics)

                env.counter.decrement()
            }

            example("changes value to 0") {
                assertThat(env.counter.current()).isZero()
            }

            example("tracks analytics event") {
                verifyOnly(env.analytics).trackEvent(Analytics.Event.CounterDecrement)
            }
        }
    }

    exampleGroup("decrement") {

        beforeEach {
            env.counter.decrement()
        }

        example("changes value to -1") {
            assertThat(env.counter.current()).isEqualTo(-1)
        }

        example("tracks analytics event") {
            verifyOnly(env.analytics).trackEvent(Analytics.Event.CounterDecrement)
        }

        exampleGroup("increment") {

            beforeEach {
                clearInvocations(env.analytics)

                env.counter.increment()
            }

            example("changes value to 0") {
                assertThat(env.counter.current()).isZero()
            }

            example("tracks analytics event") {
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