package dev.misasi.giancarlo.ux.renderers

import dev.misasi.giancarlo.drawing.DrawState
import dev.misasi.giancarlo.math.AffineTransform
import dev.misasi.giancarlo.math.Vector2f
import dev.misasi.giancarlo.math.Vector2i
import dev.misasi.giancarlo.ux.AppContext
import dev.misasi.giancarlo.ux.Renderer
import dev.misasi.giancarlo.ux.RendererRegistry
import dev.misasi.giancarlo.ux.View
import dev.misasi.giancarlo.ux.attributes.HorizontalAlignment
import dev.misasi.giancarlo.ux.views.VerticalLayout

class VerticalLayoutRenderer(
    private val registry: RendererRegistry
) : Renderer {
    override fun render(target: Any, context: AppContext, state: DrawState) {
        if (target !is VerticalLayout || target.hidden) return

        val contentSize = target.onMeasure(context)

        // first apply my margin
        val marginOffset = AffineTransform.translate(target.margin.tl.toVector2f())

        // now draw my background
//        val backgroundSize = target.padding.size.plus(contentSize)
//        mesh.putSprite(background, marginOffset.scale(backgroundSize.toVector2f()))

        // count visible children to evenly distribute
        val visibleChildrenCount = target.children.count { it.visible }
        if (visibleChildrenCount == 0) return

        val maxChildSize = Vector2f(contentSize.x, contentSize.y / visibleChildrenCount)
        var childAffine = marginOffset.translate(target.padding.tl.toVector2f())

        // now draw my children
        for (child in target.children) {
            if (child.visible) {
                childAffine = renderChild(
                    child,
                    context,
                    state,
                    childAffine,
                    target.horizontalAlignment,
                    maxChildSize.toVector2i(),
                )
            }
        }
    }

    private fun renderChild(
        child: View,
        context: AppContext,
        state: DrawState,
        startAffine: AffineTransform,
        alignment: HorizontalAlignment,
        maxSize: Vector2i,
    ): AffineTransform {
        val childSize = child.onMeasure(context).min(maxSize)
        val offset = when (alignment) {
            HorizontalAlignment.LEFT -> null
            HorizontalAlignment.MIDDLE -> Vector2f((maxSize.x - childSize.x) / 2f, 0)
            HorizontalAlignment.RIGHT -> Vector2f(maxSize.x - childSize.x, 0)
        }

        val affine = if (offset != null) startAffine.translate(offset) else startAffine
        state.pushAffine(affine)
        registry.render(child, context, state)
        state.popAffine()
        return affine.translate(Vector2f(0, childSize.y))
    }
}