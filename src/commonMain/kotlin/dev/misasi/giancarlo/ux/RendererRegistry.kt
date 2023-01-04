package dev.misasi.giancarlo.ux

import dev.misasi.giancarlo.drawing.DrawState
import dev.misasi.giancarlo.system.System.Companion.crash
import kotlin.reflect.KClass

class RendererRegistry {
    private val renderers = mutableMapOf<KClass<*>, Renderer>()

    fun register(kclass: KClass<*>, renderer: Renderer) {
        renderers[kclass] = renderer
    }

    fun render(target: Any, context: AppContext, state: DrawState) =
        renderer(target).render(target, context, state)

    private fun renderer(target: Any): Renderer =
        renderers[target.javaClass.kotlin] ?: crash("Renderer not found for ${target.javaClass.kotlin}.")
}