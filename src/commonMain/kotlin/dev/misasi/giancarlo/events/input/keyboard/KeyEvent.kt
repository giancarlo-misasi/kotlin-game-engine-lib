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

package dev.misasi.giancarlo.events.input.keyboard

import dev.misasi.giancarlo.system.System.Companion.crash
import dev.misasi.giancarlo.events.Event
import dev.misasi.giancarlo.math.Vector2f
import dev.misasi.giancarlo.system.System.Companion.getCurrentTimeMs

data class KeyEvent(
    override val window: Long,
    override val time: Long,
    override val absolutePosition: Vector2f,
    val key: Key,
    val action: KeyAction,
    val modifier: KeyModifier?,
    val scanCode: Int
) : Event {
    override val id: Int = Event.getNextId()

    companion object {
        fun valueOf(window: Long, position: Vector2f, keyCode: Int, scanCode: Int, actionCode: Int, modifierCode: Int): KeyEvent {
            val key = Key.valueOf(keyCode) ?: crash("Unknown key value: $keyCode, $scanCode, $actionCode, $modifierCode")
            val action = KeyAction.valueOf(actionCode) ?: crash("Unknown key value: $keyCode, $scanCode, $actionCode, $modifierCode")
            val modifier = KeyModifier.valueOf(modifierCode)
            return KeyEvent(window, getCurrentTimeMs(), position, key, action, modifier, scanCode)
        }
    }
}