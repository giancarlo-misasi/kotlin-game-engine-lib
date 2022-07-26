package dev.misasi.giancarlo.ux.rendering

import dev.misasi.giancarlo.opengl.DisplayContext

interface Renderer<Any> {
    fun prepareRender(context: DisplayContext, view: Any)
    fun render(context: DisplayContext, view: Any)
}