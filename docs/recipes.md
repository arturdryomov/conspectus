# Recipes

## Execution Listeners

The JUnit Platform supports [execution listeners](https://junit.org/junit5/docs/current/user-guide/#launcher-api-listeners-custom).
Such listeners are useful for hooking into the test run.
In fact, both [IntelliJ IDEA](https://github.com/JetBrains/intellij-community/blob/ee3f8c34c512e6b64dc8b8107213d344d677eba0/plugins/junit5_rt/src/com/intellij/junit5/JUnit5TestExecutionListener.java)
and [Gradle](https://github.com/gradle/gradle/blob/6d0b0d9da312d6f417142c504a6aaaa5d07bd253/subprojects/testing-junit-platform/src/main/java/org/gradle/api/internal/tasks/testing/junitplatform/JUnitPlatformTestExecutionListener.java)
use those for their purposes — like showing a relevant UI or generating a report.
We can use them as well.

1. Declare the JUnit Platform Launcher dependency — this is where the `TestExecutionListener` is declared.

    ```kotlin
    testImplementation("org.junit.platform:junit-platform-launcher:{{JUnit Platform version}}")
    ```

1. Declare the listener. For example, at `src/test/kotlin/{{package path}}/Listener.kt`.

    ```kotlin
    package {{package name}}

    class Listener : TestExecutionListener {

        override fun testPlanExecutionStarted(testPlan: TestPlan) {
            println("Hello there")
        }
    }
    ```

1. Declare the listener at the [`ServiceLoader`](https://docs.oracle.com/javase/10/docs/api/java/util/ServiceLoader.html)
   resource to let the JUnit Platform know about its existence. In our case it will be at
   `src/test/resources/META-INF/services/org.junit.platform.launcher.TestExecutionListener`.

    ```properties
    {{package name}}.Listener
    ```

That’s it. Take a look at the `TestExecutionListener` signature for different hooks.
It is possible to declare multiple listeners via appending entries to the resource above.

### Mockito Inline Mocks

Mockito has [memory management issues](https://github.com/mockito/mockito/issues/1614)
when using the inline mock maker. As a consequence, it is necessary to clear mocks
manually after each test. Let’s automate this.

```kotlin
class MockitoListener : TestExecutionListener {

    override fun executionFinished(testIdentifier: TestIdentifier, testExecutionResult: TestExecutionResult) {
        Mockito.framework().clearInlineMocks()
    }
}
```

### RxJava Uncaught Exceptions

Uncaught RxJava 2+ exceptions [will not fail tests](https://github.com/ReactiveX/RxJava/issues/5234).
This happens because RxJava passes such exceptions to the `UncaughtExceptionHandler` instead of thowing them.
The test engine doesn’t have a chance to record an exception and fail a test.

There is an interesting difference between platform `UncaughtExceptionHandler`
implementations. The JVM one will print a stacktrace, the Android one will stop the process.
This is fine but it is easy to expect that a test will fail on uncaught exceptions having an Android experience.

As a workaround, it is possible to use a custom `UncaughtExceptionHandler`.

```kotlin
class RxJavaListener : TestExecutionListener {

    override fun testPlanExecutionStarted(testPlan: TestPlan) {
        Thread.setDefaultUncaughtExceptionHandler { _, e ->
            throw AssertionError("Uncaught exception.", e)
        }
    }
}
```

## Parallel Execution

The JUnit Jupiter engine [supports parallel execution](https://junit.org/junit5/docs/current/user-guide/#writing-tests-parallel-execution).
The JUnit Platform provides APIs to make all engines parallel but it’s not so trivial,
taking the nesting and the shared state into account. Plus it will not work well with the global-state-based tools
like Mockito. Please open an issue describing the codebase and used testing tools and we’ll see what can be done in the future.

In the meanwhile, [the Gradle parallel test execution](https://docs.gradle.org/current/dsl/org.gradle.api.tasks.testing.Test.html#org.gradle.api.tasks.testing.Test:maxParallelForks) is recommended.