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

package dev.misasi.giancarlo.events.input.mouse

import dev.misasi.giancarlo.system.System.Companion.crash
import dev.misasi.giancarlo.events.Event
import dev.misasi.giancarlo.events.input.PositionEvent
import dev.misasi.giancarlo.events.input.keyboard.KeyModifier
import dev.misasi.giancarlo.math.Vector2f
import dev.misasi.giancarlo.system.System.Companion.getCurrentTimeMs

data class MouseButtonEvent (
    val window: Long,
    override val position: Vector2f,
    val button: MouseButton,
    val action: MouseButtonAction,
    val modifier: KeyModifier?,
    val time: Long
) : Event, PositionEvent {

    override fun withPosition(position: Vector2f): Event = copy(position = position)

    companion object {
        fun valueOf(window: Long, position: Vector2f, buttonCode: Int, actionCode: Int, modifierCode: Int): MouseButtonEvent {
            val button = MouseButton.valueOf(buttonCode) ?: crash("Unknown mbtn value: $buttonCode, $actionCode, $modifierCode")
            val action = MouseButtonAction.valueOf(actionCode) ?: crash("Unknown mbtn value: $buttonCode, $actionCode, $modifierCode")
            val modifier = KeyModifier.valueOf(modifierCode)
            return MouseButtonEvent(window, position, button, action, modifier, getCurrentTimeMs())
        }
    }
}