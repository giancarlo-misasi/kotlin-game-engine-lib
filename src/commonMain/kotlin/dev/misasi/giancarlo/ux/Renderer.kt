package dev.misasi.giancarlo.ux

import dev.misasi.giancarlo.drawing.programs.Sprite2d
import dev.misasi.giancarlo.opengl.DisplayContext
import dev.misasi.giancarlo.ux.views.View

interface Renderer<V : View> {
    fun prepareRender(context: DisplayContext, gfx: Sprite2d, view: V) = Unit
    fun render(context: DisplayContext, gfx: Sprite2d, view: V) = Unit
}