package dev.misasi.giancarlo.ux

import dev.misasi.giancarlo.assets.Assets.Companion.assets
import dev.misasi.giancarlo.drawing.programs.Sprite2d
import dev.misasi.giancarlo.ResourceContext
import dev.misasi.giancarlo.ux.renderers.BoxLayoutRenderer
import dev.misasi.giancarlo.ux.views.BoxLayout
import dev.misasi.giancarlo.ux.views.View
import kotlin.reflect.KClass

class RenderProvider private constructor(context: ResourceContext) {
    private val renderers = mutableMapOf<KClass<*>, Renderer<*>>()

    init {
        val assets = context.assets
        val temp = assets.atlas("Overworld") // todo: replace with UX specific atlas
        val background = temp.material("FloorOrange")
        registerRenderer(BoxLayout::class, BoxLayoutRenderer(background))
    }

    fun <V : View> registerRenderer(id: KClass<V>, renderer: Renderer<V>) {
        renderers[id] = renderer
    }

    fun <V : View> prepareRender(context: ResourceContext, gfx: Sprite2d, view: V) =
        renderer(view)?.prepareRender(context, gfx, view)

    fun <V : View> render(context: ResourceContext, gfx: Sprite2d, view: V) =
        renderer(view)?.render(context, gfx, view)

    private inline fun <V : View, reified R : Renderer<V>> renderer(view: V): R? {
        val renderer = renderers[view::class] ?: return null
        return if (renderer is R) {
            renderer
        } else {
            throw IllegalStateException("Missing renderer for view: ${view::class}")
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: RenderProvider? = null

        private fun getInstance(context: ResourceContext): RenderProvider =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: RenderProvider(context).also { INSTANCE = it }
            }

        val ResourceContext.renderers: RenderProvider
            get() = getInstance(this)
    }
}