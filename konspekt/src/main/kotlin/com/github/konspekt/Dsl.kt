package com.github.konspekt

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@DslMarker
annotation class Dsl

@Dsl
interface Example

@Dsl
interface ExampleGroup {
    fun <T : Any> memoized(creator: () -> T): Memoized<T>

    fun example(name: String, action: Example.() -> Unit)
    fun exampleGroup(name: String, action: ExampleGroup.() -> Unit)

    fun describe(name: String, action: ExampleGroup.() -> Unit) = exampleGroup("describe $name", action)
    fun context(name: String, action: ExampleGroup.() -> Unit) = exampleGroup("context $name", action)
    fun it(name: String, action: Example.() -> Unit) = example("it $name", action)

    fun beforeEach(action: () -> Unit)
    fun afterEach(action: () -> Unit)
}