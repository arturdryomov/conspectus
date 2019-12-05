# Usage

## Concepts

* Example — a condition, a check, a test.
  Follows [RSpec naming](https://rubydoc.info/github/rspec/rspec-core/RSpec/Core/Example).
  See [Specification by Example](https://en.wikipedia.org/wiki/Specification_by_example).
* Example Group — a group of examples.

It is important to understand that specifications don’t have a notion of tests per se.
xUnit tests are broken into two concepts: actions and checks.

```kotlin
@Test fun `increment changes value`() {
    // This is an action.
    counter.increment()

    // This is a check.
    assertThat(counter.current()).isEqual(1)
}
```
```kotlin
context("increment") {

    // This is an action.
    beforeEach {
        counter.increment()
    }

    // This is a check.
    it("changes value") {
        assertThat(counter.current()).isEqual(1)
    }
}
```

Take a closer look again — tests are trying to be two things at once,
specifications make them atomic and dedicated. When this idea _clicks_ —
there is no way back to xUnit.

## Specification

Specifications are declared as `Spec` subclasses.
`Spec` receives a lambda on a top-level Example Group which allows to declare
further Example Groups and Examples.

```kotlin
class CounterSpec : Spec({

    val counter by memoized { Counter.Impl() }

    it("contains default value") {
        assertThat(counter.current()).isZero()
    }
})
```

## DSL

Example Groups can declare Examples, Example Groups and a bit more.
Examples cannot declare anything.

### `context` and `describe`

Declares an Example Group.

```kotlin
context("nothing") {

    it("does not change value") {
        assertThat(counter.current()).isEqual(0)
    }
}
```

`describe` is a `context` synonym. Use it for grouping `context` declarations
when necessary.

### `it`

Declares an Example.

```kotlin
it("changes value") {
    assertThat(counter.current()).isEqual(1)
}
```

### `fdescribe`, `fcontext` and `fit`

**F**ocuses an Example Group or an Example. Focusing means executing focused entries
and nothing else.

```kotlin
// Skipped.
it("tracks analytics") {
    verify(env.analytics).trackEvent(Analytics.Event.CounterIncrement)
}

// Executed.
fit("changes value") {
    assertThat(counter.current()).isEqual(1)
}
```

### `xdescribe`, `xcontext`, `xit`

E**x**cludes an Example Group or an Example. Excluding means skipping excluded entries.

```kotlin
// Executed.
it("tracks analytics") {
    verify(env.analytics).trackEvent(Analytics.Event.CounterIncrement)
}

// Skipped.
xit("changes value") {
    assertThat(counter.current()).isEqual(1)
}
```

### `beforeEach` and `afterEach`

Declares an action executed before (`beforeEach`) or after (`afterEach`) an example.

```kotlin
beforeEach {
    counter.increment()
}
```

### `memoized`

Declares a special delegate. See [RSpec `let`](https://relishapp.com/rspec/rspec-core/v/3-9/docs/helper-methods/let-and-let).

* The delegated object is created on the first access.
* The object is cached during an example execution.
* The object is removed after the example execution.

```kotlin
val counter by memoized { Counter.Impl() }
```

This is a direct equivalent of the following xUnit declaration.

```kotlin
var counter: Counter? = null

@BeforeEach fun setUp() {
    counter = Counter.Impl()
}

@AfterEach fun tearDown() {
    counter = null
}
```
