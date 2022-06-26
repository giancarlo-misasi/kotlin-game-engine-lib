package dev.misasi.giancarlo.events.lifecycle

import dev.misasi.giancarlo.events.Event

data class LifecycleEvent (
    val stage: Stage,
    val data: Any?
) : Event {
    enum class Stage {
        Create,
        Start,
        Resume,
        Pause,
        Stop,
        Destroy
    }
}
