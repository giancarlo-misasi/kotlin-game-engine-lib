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
import dev.misasi.giancarlo.events.EventQueue
import dev.misasi.giancarlo.events.input.keyboard.KeyEvent
import dev.misasi.giancarlo.events.input.mouse.CursorMode
import dev.misasi.giancarlo.events.input.mouse.MouseButtonEvent
import dev.misasi.giancarlo.events.input.mouse.MouseEvent
import dev.misasi.giancarlo.events.input.scroll.ScrollEvent
import dev.misasi.giancarlo.events.input.text.TextEvent
import dev.misasi.giancarlo.events.input.window.ResizeEvent
import dev.misasi.giancarlo.math.Vector2i

class Window(
    private val gl: OpenGl,
    title: String,
    size: Vector2i,
    fullScreen: Boolean,
    refreshRate: Int,
    vsync: Boolean,
    private val designedResolution: Vector2i,
) {
    private val window = gl.createWindow(title, size, fullScreen, refreshRate, vsync)
    private val events = EventQueue(setOf()) // todo: add detectors as needed

    var viewport = createViewport()
        private set

    fun setFullScreen(size: Vector2i, refreshRate: Int) {
        gl.setFullScreen(window, size, refreshRate)
        viewport = createViewport()
    }

    fun setWindowed(size: Vector2i) {
        gl.setWindowed(window, size)
        viewport = createViewport()
    }

    fun setVsync(vsync: Boolean) = gl.setVsync(window, vsync)
    fun setCursorMode(mode: CursorMode) = gl.setCursorMode(window, mode)
    fun swapBuffers() = gl.swapBuffers(window)
    fun shouldClose(): Boolean = gl.shouldCloseWindow(window)
    fun close() = gl.closeWindow(window)

    fun enableResizeEvents(enable: Boolean) = gl.onResizeEvents(window, if (enable) ::onResizeEvent else null)
    fun enableKeyboardEvents(enable: Boolean) = gl.onKeyboardEvents(window, if (enable) ::onKeyboardEvent else null)
    fun enableTextEvents(enable: Boolean) = gl.onTextEvents(window, if (enable) ::onTextEvent else null)
    fun enableMouseEvents(enable: Boolean) = gl.onMouseEvents(window, if (enable) ::onMouseEvent else null)
    fun enableMouseButtonEvents(enable: Boolean) = gl.onMouseButtonEvents(window, if (enable) ::onMouseButtonEvent else null)
    fun enableScrollEvents(enable: Boolean) = gl.onScrollEvents(window, if (enable) ::onScrollEvent else null)
    fun getNextEvent(): Event? = events.getNextEvent()

    private fun createViewport(): Viewport {
        val windowSize = gl.getActualWindowSize(window)
        return Viewport.create(windowSize, designedResolution)
    }

    private fun onKeyboardEvent(window: Long, keyCode: Int, scanCode: Int, actionCode: Int, modifierCode: Int) {
        events.pushEvent(KeyEvent.valueOf(window, keyCode, scanCode, actionCode, modifierCode))
    }

    private fun onTextEvent(window: Long, codePoint: Int) {
        events.pushEvent(TextEvent.valueOf(window, codePoint))
    }

    private fun onMouseEvent(window: Long, x: Double, y: Double) {
        events.pushPositionEvent(viewport, MouseEvent.valueOf(window, x, y))
    }

    private fun onMouseButtonEvent(window: Long, buttonCode: Int, actionCode: Int, modifierCode: Int) {
        val position = gl.getMousePosition(window)
        events.pushPositionEvent(viewport, MouseButtonEvent.valueOf(window, position, buttonCode, actionCode, modifierCode))
    }

    private fun onScrollEvent(window: Long, x: Double, y: Double) {
        events.pushEvent(ScrollEvent.valueOf(window, x, y))
    }

    private fun onResizeEvent(window: Long, x: Int, y: Int) {
        events.pushEvent(ResizeEvent.valueOf(window, x, y))
        viewport = createViewport()
    }
}