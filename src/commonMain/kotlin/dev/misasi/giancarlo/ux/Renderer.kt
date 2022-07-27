package dev.misasi.giancarlo.ux

import dev.misasi.giancarlo.drawing.programs.Sprite2d
import dev.misasi.giancarlo.ResourceContext
import dev.misasi.giancarlo.ux.views.View

interface Renderer<V : View> {
    fun prepareRender(context: ResourceContext, gfx: Sprite2d, view: V) = Unit
    fun render(context: ResourceContext, gfx: Sprite2d, view: V) = Unit
}