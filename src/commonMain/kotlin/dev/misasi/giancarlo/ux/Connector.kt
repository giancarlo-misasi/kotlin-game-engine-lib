package dev.misasi.giancarlo.ux

import dev.misasi.giancarlo.events.Event
import dev.misasi.giancarlo.ResourceContext
import dev.misasi.giancarlo.ux.views.Screen
import kotlin.reflect.KClass

interface Connector<ViewModel> {
    fun getViewModel(): ViewModel
    fun getRenderer(): KClass<Renderer<Screen<ViewModel>>>
    fun onUpdate(context: ResourceContext, elapsedMs: Long)
    fun onEvent(context: ResourceContext, event: Event): Boolean
}