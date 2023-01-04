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

import dev.misasi.giancarlo.assets.Assets
import dev.misasi.giancarlo.events.input.mouse.CursorMode
import dev.misasi.giancarlo.math.Vector2i
import dev.misasi.giancarlo.openal.OpenAl
import dev.misasi.giancarlo.opengl.OpenGl
import dev.misasi.giancarlo.opengl.Viewport
import dev.misasi.giancarlo.system.Clock
import dev.misasi.giancarlo.ux.transitions.Fade
import dev.misasi.giancarlo.ux.transitions.Transition

interface AppContext {
    val gl: OpenGl
    val al: OpenAl
    val clock: Clock
    val assets: Assets
    val viewport: Viewport

    fun go(
        nextRootView: View,
        outTransition: Transition = Fade.fadeOut(),
        inTransition: Transition = Fade.fadeIn(1000),
    )
    fun setFullScreen(size: Vector2i, refreshRate: Int)
    fun setWindowed(size: Vector2i)
    fun setVsync(vsync: Boolean)
    fun setCursorMode(mode: CursorMode)
    fun enableResizeEvents(enable: Boolean)
    fun enableKeyboardEvents(enable: Boolean)
    fun enableTextEvents(enable: Boolean)
    fun enableMouseEvents(enable: Boolean)
    fun enableMouseButtonEvents(enable: Boolean)
    fun enableScrollEvents(enable: Boolean)
    fun close()
}