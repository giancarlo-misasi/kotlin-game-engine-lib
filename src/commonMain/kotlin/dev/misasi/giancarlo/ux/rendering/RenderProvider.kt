package dev.misasi.giancarlo.ux.rendering

import dev.misasi.giancarlo.opengl.DisplayContext
import kotlin.reflect.KClass

class RenderProvider : Renderer<Any> {
    private val instances = mapOf<KClass<Any>, Renderer<Any>>()

    override fun prepareRender(context: DisplayContext, view: Any) =
        provide(view).prepareRender(context, view)

    override fun render(context: DisplayContext, view: Any) =
        provide(view).render(context, view)

    // todo: do more here
    // also, actually wire up all the providers
    private fun provide(view: Any): Renderer<Any> = instances[view.javaClass.kotlin]!!
}