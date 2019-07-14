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

    fun beforeEach(action: () -> Unit)
    fun afterEach(action: () -> Unit)
}