package com.github.konspekt

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@DslMarker
private annotation class Dsl

@Dsl
interface Example

@Dsl
interface ExampleGroup {
    fun <T : Any> memoized(creator: () -> T): Memoized<T>

    fun example(name: String, marker: Marker? = null, action: Example.() -> Unit)
    fun exampleGroup(name: String, marker: Marker? = null, action: ExampleGroup.() -> Unit)

    fun describe(name: String, action: ExampleGroup.() -> Unit) = exampleGroup("describe $name", null, action)
    fun context(name: String, action: ExampleGroup.() -> Unit) = exampleGroup("context $name", null, action)
    fun it(name: String, action: Example.() -> Unit) = example("it $name", null, action)

    fun fdescribe(name: String, action: ExampleGroup.() -> Unit) = exampleGroup("fdescribe $name", Marker.Include, action)
    fun fcontext(name: String, action: ExampleGroup.() -> Unit) = exampleGroup("fcontext $name", Marker.Include, action)
    fun fit(name: String, action: Example.() -> Unit) = example("fit $name", Marker.Include, action)

    fun xdescribe(name: String, action: ExampleGroup.() -> Unit) = exampleGroup("xdescribe $name", Marker.Exclude, action)
    fun xcontext(name: String, action: ExampleGroup.() -> Unit) = exampleGroup("xcontext $name", Marker.Exclude, action)
    fun xit(name: String, action: Example.() -> Unit) = example("xit $name", Marker.Exclude, action)

    fun beforeEach(action: () -> Unit)
    fun afterEach(action: () -> Unit)
}

enum class Marker { Include, Exclude }