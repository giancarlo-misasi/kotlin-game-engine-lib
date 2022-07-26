package dev.misasi.giancarlo.ux.rendering

import dev.misasi.giancarlo.drawing.Material
import dev.misasi.giancarlo.drawing.programs.Sprite2d
import dev.misasi.giancarlo.math.Vector2f
import dev.misasi.giancarlo.math.Vector2i
import dev.misasi.giancarlo.opengl.DisplayContext
import dev.misasi.giancarlo.ux.BoxLayout
import dev.misasi.giancarlo.ux.View
import dev.misasi.giancarlo.ux.attributes.HorizontalAlignment
import dev.misasi.giancarlo.ux.attributes.LayoutAlignment
import dev.misasi.giancarlo.ux.attributes.LayoutDirection
import dev.misasi.giancarlo.ux.attributes.VerticalAlignment

class BoxLayoutRenderer(
    private val renderProvider: RenderProvider,
    private val background: Material,
    private val gfx: Sprite2d
) : Renderer<BoxLayout> {

    override fun prepareRender(context: DisplayContext, view: BoxLayout) {
        if (!view.visible) return

        val contentSize = view.onMeasure(context)
        val backgroundSize = view.inset.padding.size.plus(contentSize)

        // first apply my margin
        gfx.pushOffset(view.inset.margin.tl.toVector2f())

        // now draw my background
        gfx.putSprite(Vector2f(), backgroundSize.toVector2f(), background)

        // now apply my padding
        gfx.pushOffset(view.inset.padding.tl.toVector2f())

        // now draw my children
        var drawCount = 0
        for (child in view.children) {
            if (child.visible) {
                prepareRenderChild(context, view.layoutDirection, view.layoutAlignment, contentSize, child)
                drawCount++
            }
        }

        // finally, pop the margin, padding and child offsets
        gfx.popOffset(drawCount + 2)
    }

    override fun render(context: DisplayContext, view: BoxLayout) = Unit

    private fun prepareRenderChild(
        context: DisplayContext,
        layoutDirection: LayoutDirection,
        layoutAlignment: LayoutAlignment,
        maxSize: Vector2i,
        child: View
    ) {
        when (layoutDirection) {
            LayoutDirection.HORIZONTAL -> prepareRenderChildHorizontal(context, layoutAlignment, maxSize, child)
            LayoutDirection.VERTICAL -> prepareRenderChildVertical(context, layoutAlignment, maxSize, child)
        }
    }

    private fun prepareRenderChildHorizontal(
        context: DisplayContext,
        layoutAlignment: LayoutAlignment,
        maxSize: Vector2i,
        child: View
    ) {
        val childSize = child.onMeasure(context).min(maxSize)
        val offset = when (layoutAlignment.y) {
            VerticalAlignment.TOP -> null
            VerticalAlignment.MIDDLE -> Vector2f(0, (maxSize.y - childSize.y) / 2f)
            VerticalAlignment.BOTTOM -> Vector2f(0, maxSize.y - childSize.y)
        }

        if (offset != null) {
            gfx.pushOffset(offset)
            renderProvider.prepareRender(context, child)
            gfx.popOffset()
        } else {
            renderProvider.prepareRender(context, child)
        }

        gfx.pushOffset(Vector2f(childSize.x, 0))
    }

    private fun prepareRenderChildVertical(
        context: DisplayContext,
        layoutAlignment: LayoutAlignment,
        maxSize: Vector2i,
        child: View
    ) {
        val childSize = child.onMeasure(context).min(maxSize)
        val offset = when (layoutAlignment.x) {
            HorizontalAlignment.LEFT -> null
            HorizontalAlignment.MIDDLE -> Vector2f((maxSize.x - childSize.x) / 2f, 0)
            HorizontalAlignment.RIGHT -> Vector2f(maxSize.x - childSize.x, 0)
        }

        if (offset != null) {
            gfx.pushOffset(offset)
            renderProvider.prepareRender(context, child)
            gfx.popOffset()
        } else {
            renderProvider.prepareRender(context, child)
        }

        gfx.pushOffset(Vector2f(0, childSize.y))
    }
}