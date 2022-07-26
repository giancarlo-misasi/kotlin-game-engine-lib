package dev.misasi.giancarlo.ux.rendering

import dev.misasi.giancarlo.opengl.DisplayContext

class ScreenRenderer : Renderer<Any> {

    override fun prepareRender(context: DisplayContext, view: Any) {
        // this is how we can separate screen/view state from rendering code which is nice :)
        //gfx.putSprite(Vector2f(), context.view.targetResolution.toVector2f(), temp.atlas("Overworld").material("FloorPurple"))
    }

    override fun render(context: DisplayContext, view: Any) {
        TODO("Not yet implemented")
    }
}