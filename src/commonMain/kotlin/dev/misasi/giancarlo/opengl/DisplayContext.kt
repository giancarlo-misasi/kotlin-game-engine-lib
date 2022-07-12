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

package dev.misasi.giancarlo.opengl

import dev.misasi.giancarlo.events.Event
import dev.misasi.giancarlo.events.input.mouse.CursorMode
import dev.misasi.giancarlo.math.Vector2f

interface DisplayContext {

    var title: String
    var targetResolution: Vector2f
    var windowSize: Vector2f
    var fullScreen: Boolean
    var vsync: Boolean
    var refreshRate: Int?
    val gl: OpenGl
    val viewport: Viewport

    fun getPrimaryMonitorResolution(): Vector2f
    fun getActualWindowSize(): Vector2f
    fun reconfigure()
    fun swapBuffers()

    fun enableKeyboardEvents(enable: Boolean)
    fun enableTextEvents(enable: Boolean)
    fun enableMouseEvents(enable: Boolean)
    fun enableMouseButtonEvents(enable: Boolean)
    fun enableScrollEvents(enable: Boolean)
    fun enableResizeEvents(enable: Boolean)
    fun setCursorMode(mode: CursorMode)
    fun pollEvents()
    fun getNextEvent() : Event?

    fun close()
    fun shouldClose(): Boolean
}