package com.github.konspekt

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class Memoized<out T : Any>(private val creator: () -> T) : ReadOnlyProperty<Any?, T> {

    private var value: T? = null

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        if (value == null) {
            value = creator.invoke()
        }

        return value!!
    }

    fun reset() {
        value = null
    }
}