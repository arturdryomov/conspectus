# Motivation

Take a look at the following xUnit-style JUnit test suite.

```kotlin
lateinit var counter: Counter
lateinit var analytics: Analytics

@Before fun setUp() {
    analytics = mock<Analytics>()
    counter = Counter.Impl(analytics)
}

@Test fun `increment changes value`() {
    counter.increment()

    assertThat(counter.current()).isEqualTo(1)
}

@Test fun `increment tracks analytics`() {
    counter.increment()

    verify(analytics).trackEvent(Analytics.Event.CounterIncrement)
}

@Test fun `decrement after increment changes value`() {
    counter.increment()
    counter.decrement()

    assertThat(counter.current()).isEqualTo(0)
}

@Test fun `decrement after increment tracks analytics`() {
    counter.increment()
    counter.decrement()

    verify(analytics).trackEvent(Analytics.Event.CounterDecrement)
}
```

The following specification is an equivalent of the test suite above.

```kotlin
val analytics by memoized { mock<Analytics>() }
val counter by memoized { Counter.Impl(analytics) }

context("increment") {

    beforeEach {
        counter.increment()
    }

    it("changes value") {
        assertThat(counter.current()).isEqualTo(1)
    }

    it("tracks analytics") {
        verify(analytics).trackEvent(Analytics.Event.CounterIncrement)
    }

    context("decrement") {

        beforeEach {
            counter.decrement()
        }

        it("changes value") {
            assertThat(counter.current()).isEqualTo(0)
        }

        it("tracks analytics") {
            verify(analytics).trackEvent(Analytics.Event.CounterDecrement)
        }
    }
}
```

## Differences

### One test — one check

This is achievable in xUnit but consequitive tests grow the instructions count.
Doing an action A, then an action B and then a C should produce three tests.
If each action produces two effects then the count grows to six.
Specifications split the xUnit test concept into two: actions and checks.
At the same time, specifications provide nesting — a single action can be followed
by multiple checks or / and additional actions. This prevents the code duplication.

### Hermetic Tests

A test should be executed in an isolated environment, not shared with tests
of the same test suite. xUnit achieves this via set up methods which
are invoked before each test. The same behavior is achievable in specifications.

```kotlin
lateinit var analytics: Analytics

beforeEach {
    analytics = mock<Analytics>()
}
```

However, there is a better tool — `memoized` containers.
Such objects will be destroyed after each test automatically.

### Naming and Structure

Since xUnit tests are methods there are clever hacks to name them better —
`increment changes value` instead of `testIncrementChangesValue`.
Specifications use function invocations, so it is possible
to define arbitary text as a test name. At the same time, the hierarchy
provides a test tree in the IDE instead of a long list of similar tests with no
ability to distinguish nesting. This makes tests declarative,
easier to read and, as a consequence, to extend and maintain.

### Bonus: Language Features

Specifications use function invocations instead of functions.
This makes it possible to use Kotlin conditions and loops.

```kotlin
enum CounterCondition { Fine, Broken }

CounterCondition.values().forEach { counterCondition ->

    describe("counter condition [$counterCondition]") {

        val analytics by memoized { mock<Analytics>() }
        val counter by memoized { Counter.Impl(counterCondition, analytics) }

        context("increment") {

            beforeEach {
                counter.increment()
            }

            it("tracks analytics") {
                verify(analytics).trackEvent(Analytics.Event.CounterIncrement)
            }

            if (counterCondition == CounterCondition.Fine) {

                it("changes value") {
                    assertThat(counter.current()).isEqualTo(1)
                }

            } else {

                it("does not change value") {
                    assertThat(counter.current()).isEqualTo(0)
                }

            }
        }
    }
}
```
