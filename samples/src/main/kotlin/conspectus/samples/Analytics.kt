package conspectus.samples

interface Analytics {

    enum class Event {
        CounterIncrement,
        CounterDecrement,
    }

    fun trackEvent(event: Event)

    class Impl : Analytics {

        override fun trackEvent(event: Event) = println("Analytics event: [${event.name}]")
    }
}