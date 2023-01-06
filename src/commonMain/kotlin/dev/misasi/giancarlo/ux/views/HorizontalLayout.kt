package dev.misasi.giancarlo.ux.views

import dev.misasi.giancarlo.drawing.DrawOptions
import dev.misasi.giancarlo.drawing.DrawState
import dev.misasi.giancarlo.math.AffineTransform
import dev.misasi.giancarlo.math.Rectangle
import dev.misasi.giancarlo.math.Vector2f
import dev.misasi.giancarlo.math.Vector2i
import dev.misasi.giancarlo.system.System.Companion.crash
import dev.misasi.giancarlo.ux.AppContext
import dev.misasi.giancarlo.ux.ViewGroup
import dev.misasi.giancarlo.ux.attributes.HorizontalAlignment
import dev.misasi.giancarlo.ux.attributes.VerticalAlignment

class HorizontalLayout : ViewGroup() {
    var verticalAlignment = VerticalAlignment.MIDDLE
    var horizontalAlignment = HorizontalAlignment.MIDDLE

    override fun onSize(context: AppContext, maxSize: Vector2i) {
        if (inset.size.x > maxSize.x || inset.size.y > maxSize.y) {
            crash("Layout impossible, not enough space.") // todo
        }

        val c = visibleChildren()
        val w = calculateWidth(c)
        val h = calculateMaxHeight(c, maxSize.y)
        if (w > maxSize.x || h > maxSize.y) {
            crash("Layout impossible, not enough space.") // todo
        }

        // Update my size
        size = Vector2i(w, h)

        // Adjust if I have children that need sizing
        val childrenNoSize = c.filter { it.size == null }
        if (childrenNoSize.isEmpty()) return

        val remainingWidth = maxSize.x - w
        if (remainingWidth < childrenNoSize.size) {
            crash("Layout impossible, not enough space.") // todo
        }

        // Set sizes for un-sized children
        val wSplit = remainingWidth / childrenNoSize.size
        for (child in childrenNoSize) {
            child.onSize(context, Vector2i(wSplit, h))
        }

        // Update my size, now that my children have been re-sized
        size = Vector2i(calculateWidth(c), calculateMaxHeight(c, maxSize.y))
    }

    override fun onLayout(context: AppContext, state: DrawState, scissor: Rectangle) {
        val s = size ?: return
        val w = calculateWidth(visibleChildren())

        // Calculate horizontal alignment offset
        var x: Int = when(horizontalAlignment) {
            HorizontalAlignment.LEFT -> 0
            HorizontalAlignment.MIDDLE -> s.x.minus(w).div(2)
            HorizontalAlignment.RIGHT -> s.x.minus(w)
        }

        visibleChildren().forEach { child ->
            val cx = child.size?.x ?: 0
            val cy = child.size?.y ?: 0

            // Calculate vertical alignment offset
            val y: Int = when(verticalAlignment) {
                VerticalAlignment.TOP -> 0
                VerticalAlignment.MIDDLE -> s.y.minus(cy).div(2)
                VerticalAlignment.BOTTOM -> s.x.minus(cy)
            }

            // Draw the child with the appropriate transforms
            val affine = AffineTransform.translate(Vector2f(x, y))
            state.applyOptionsScoped(DrawOptions(scissor, affine)) {
                child.onUpdateDrawState(context, state)
            }

            // Move next child over to my right
            x += cx
        }
    }
}