# Conspectus

A specification test engine for Kotlin based on the JUnit Platform.
Inspirations: [RSpec for Ruby](https://en.wikipedia.org/wiki/RSpec) and [Quick for Swift](https://github.com/Quick/Quick/).

Specifications are a declarative, scalable alternative to xUnit-style tests:

```kotlin
val counter by memoized { Counter() }

context("increment") {

    beforeEach {
        counter.increment()
    }

    it("changes value to 1") {
        assertThat(counter.current()).isEqualTo(1)
    }

    context("decrement") {

        beforeEach {
            counter.decrement()
        }

        it("changes value to 0") {
            assertThat(counter.current()).isEqualTo(0)
        }
    }
}
```

## FAQ

### Should I use this?

No.

## License

```
Copyright 2019 Artur Dryomov

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

