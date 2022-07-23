/*
 * MIT License
 *
 * Copyright (c) 2022 Giancarlo Misasi
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package dev.misasi.giancarlo.ux

import dev.misasi.giancarlo.drawing.Material
import dev.misasi.giancarlo.drawing.programs.Sprite2d
import dev.misasi.giancarlo.math.Vector2f
import dev.misasi.giancarlo.math.Vector2i
import dev.misasi.giancarlo.opengl.DisplayContext
import dev.misasi.giancarlo.ux.attributes.*

class SimpleBoxLayoutViewGroup (
    var background: Material,
    var margin: LayoutMargin = LayoutMargin(),
    var layoutDirection: LayoutDirection = LayoutDirection.HORIZONTAL,
    var layoutAlignment: LayoutAlignment = LayoutAlignment()
) : ViewGroup() {

    override fun onMeasure(): Vector2i {
        return margin.plus(when (layoutDirection) {
            LayoutDirection.HORIZONTAL -> horizontalMinimumSize()
            LayoutDirection.VERTICAL -> verticalMinimumSize()
        })
    }

    override fun onDraw(context: DisplayContext, gfx: Sprite2d) {
        if (!visible) return

        val size = onMeasure()

        // first apply my margin
        gfx.pushOffset(margin.tl.toVector2f())

        // now draw my background
        gfx.putSprite(Vector2f(), size.toVector2f(), background)

        // now draw my children
        var drawCount = 0
        for (child in children) {
            if (child.visible) {
                drawChild(context, gfx, size, child)
                drawCount++
            }
        }

        // finally, pop the margin and child offsets
        gfx.popOffset(drawCount + 1)
    }

    private fun drawChild(context: DisplayContext, gfx: Sprite2d, parentSize: Vector2i, child: View) {
        when (layoutDirection) {
            LayoutDirection.HORIZONTAL -> drawChildHorizontal(context, gfx, parentSize, child)
            LayoutDirection.VERTICAL -> drawChildVertical(context, gfx, parentSize, child)
        }
    }

    private fun drawChildHorizontal(context: DisplayContext, gfx: Sprite2d, parentSize: Vector2i, child: View) {
        val childSize = child.onMeasure()
        val offset = when (layoutAlignment.y) {
            VerticalAlignment.TOP -> null
            VerticalAlignment.MIDDLE -> Vector2f(0, (parentSize.y - childSize.y) / 2f)
            VerticalAlignment.BOTTOM -> Vector2f(0, parentSize.y - childSize.y)
        }

        if (offset != null) {
            gfx.pushOffset(offset)
            child.onDraw(context, gfx)
            gfx.popOffset()
        } else {
            child.onDraw(context, gfx)
        }

        gfx.pushOffset(Vector2f(childSize.x, 0))
    }

    private fun drawChildVertical(context: DisplayContext, gfx: Sprite2d, parentSize: Vector2i, child: View) {
        val childSize = child.onMeasure()
        val offset = when (layoutAlignment.x) {
            HorizontalAlignment.LEFT -> null
            HorizontalAlignment.MIDDLE -> Vector2f((parentSize.x - childSize.x) / 2f, 0)
            HorizontalAlignment.RIGHT -> Vector2f(parentSize.x - childSize.x, 0)
        }

        if (offset != null) {
            gfx.pushOffset(offset)
            child.onDraw(context, gfx)
            gfx.popOffset()
        } else {
            child.onDraw(context, gfx)
        }

        gfx.pushOffset(Vector2f(0, childSize.y))
    }

    private fun horizontalMinimumSize(): Vector2i {
        val measurements = children.map { it.onMeasure() }
        return Vector2i(measurements.sumOf { it.x }, measurements.maxOf { it.y })
    }

    private fun verticalMinimumSize(): Vector2i {
        val measurements = children.map { it.onMeasure() }
        return Vector2i(measurements.maxOf { it.x }, measurements.sumOf { it.y })
    }
}