package dev.misasi.giancarlo.ux

import dev.misasi.giancarlo.events.Event
import dev.misasi.giancarlo.opengl.DisplayContext
import dev.misasi.giancarlo.ux.views.Screen
import kotlin.reflect.KClass

interface Connector<ViewModel> {
    fun getViewModel(): ViewModel
    fun getRenderer(): KClass<Renderer<Screen<ViewModel>>>
    fun onUpdate(context: DisplayContext, elapsedMs: Long)
    fun onEvent(context: DisplayContext, event: Event): Boolean
}