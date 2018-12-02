package com.github.konspekt.samples

import com.github.konspekt.Spec
import org.assertj.core.api.Assertions.assertThat
import org.junit.platform.commons.annotation.Testable

@Testable
class CounterSpec : Spec({

    val counter: Counter by memoized { Counter.Impl() }

    example("value is 0") {
        assertThat(counter.current()).isZero()
    }

    exampleGroup("increment") {

        beforeEach {
            counter.increment()
        }

        example("changes value to 1") {
            assertThat(counter.current()).isEqualTo(1)
        }

        exampleGroup("decrement") {

            beforeEach {
                counter.decrement()
            }

            example("changes value to 0") {
                assertThat(counter.current()).isZero()
            }
        }
    }

    exampleGroup("decrement") {

        beforeEach {
            counter.decrement()
        }

        example("changes value to -1") {
            assertThat(counter.current()).isEqualTo(-1)
        }

        exampleGroup("increment") {

            beforeEach {
                counter.increment()
            }

            example("changes value to 0") {
                assertThat(counter.current()).isZero()
            }
        }
    }

})