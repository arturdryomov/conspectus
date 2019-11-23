package conspectus

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class Memoized<out T : Any>(private val creator: () -> T) : ReadOnlyProperty<Any?, T> {

    private var value: T? = null

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return value ?: creator().apply { value = this }
    }

    internal fun reset() {
        value = null
    }
}