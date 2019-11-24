package conspectus

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class Memoized<out T : Any>(private val creator: () -> T) : ReadOnlyProperty<Any?, T> {

    private var value: T? = null
    private var enabled: Boolean = false

    internal fun setUp() {
        value = null
        enabled = true
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        check(enabled) { "Memoized value accessed outside of execution actions. This is blocked to avoid state management issues." }

        return value ?: creator().apply { value = this }
    }

    internal fun tearDown() {
        value = null
        enabled = false
    }
}