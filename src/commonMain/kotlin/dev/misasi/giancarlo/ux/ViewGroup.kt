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

import dev.misasi.giancarlo.drawing.DrawOptions
import dev.misasi.giancarlo.drawing.DrawState
import dev.misasi.giancarlo.events.Event
import dev.misasi.giancarlo.math.AffineTransform
import dev.misasi.giancarlo.math.Rectangle
import dev.misasi.giancarlo.math.Vector2f
import dev.misasi.giancarlo.math.Vector2i

abstract class ViewGroup : View() {
    protected var children = mutableListOf<View>()
        private set

    fun add(child: View) {
        children.add(child)
        child.parent = this
    }

    /**
     * Sets the size based on children if not already set.
     */
    abstract override fun onSize(context: AppContext, maxSize: Vector2i)

    override fun onUpdateDrawState(context: AppContext, state: DrawState) {
        if (!visible) return
        val s = size?.toVector2f() ?: return

        // Want to limit child drawing to parent, so create scissor using my absolute bounds
        val relativeOffset = AffineTransform.translate(inset.size.toVector2f())
        state.applyOptionsScoped(DrawOptions(affine = relativeOffset)) { drawOptions ->
            val absoluteScissorPosition = drawOptions.translate(relativeOffset.translation)
            onLayout(context, state, Rectangle(absoluteScissorPosition, s))
        }
    }

    /**
     * Set children's size and applies transforms to allow relative drawing.
     */
    protected abstract fun onLayout(context: AppContext, state: DrawState, scissor: Rectangle)

    override fun onElapsed(context: AppContext, elapsedMs: Long) {
        if (!visible) return
        visibleChildren().forEach {
            it.onElapsed(context, elapsedMs)
        }
    }

    override fun onEvent(context: AppContext, event: Event): Boolean {
        if (!visible) return false
        visibleChildren().forEach {
            if (it.onEvent(context, event)) {
                return true
            }
        }
        return false
    }

    protected fun visibleChildren() = children.filter { it.visible }

    protected fun calculateWidth(views: List<View>) = inset.horizontal + views.mapNotNull { it.size?.x }.sum()

    protected fun calculateHeight(views: List<View>) = inset.vertical + views.mapNotNull { it.size?.y }.sum()

    protected fun calculateMaxWidth(views: List<View>, max: Int): Int {
        return inset.horizontal + (views.mapNotNull { it.size?.x }.maxOrNull() ?: (max - inset.horizontal))
    }

    protected fun calculateMaxHeight(views: List<View>, max: Int): Int {
        return inset.vertical + (views.mapNotNull { it.size?.y }.maxOrNull() ?: (max - inset.vertical))
    }
}