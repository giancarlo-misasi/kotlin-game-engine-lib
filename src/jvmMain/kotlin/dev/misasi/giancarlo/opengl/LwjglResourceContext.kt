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

import dev.misasi.giancarlo.ResourceContext
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
import dev.misasi.giancarlo.openal.OpenAl
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryStack.stackPop
import org.lwjgl.system.MemoryStack.stackPush

class LwjglResourceContext(
    title: String,
    designedResolution: Vector2i,
    windowSize: Vector2i,
    fullScreen: Boolean = false,
    refreshRate: Int = 60
) : ResourceContext {
    private val window: Long
    override val gl: OpenGl
    override val al: OpenAl
    override val viewport: Viewport
    override val events: EventQueue

    init {
        window = initializeGlfwWindow(title, fullScreen)
        gl = LwjglOpenGl.gl
        al = LwjglOpenAl.al
        viewport = Viewport(designedResolution, if (fullScreen) designedResolution else getActualWindowSize())
        events = EventQueue(setOf()) // todo: add detectors as needed

        if (fullScreen) setFullScreen(refreshRate) else setWindowed(windowSize)
    }

    override fun setTitle(title: String) {
        glfwSetWindowTitle(window, title)
    }

    override fun setFullScreen(refreshRate: Int) {
        val monitor = glfwGetPrimaryMonitor()
        val resolution = getPrimaryMonitorResolution()

        glfwSetWindowSize(window, resolution.x, resolution.y)
        glfwSetWindowMonitor(window, monitor, 0, 0, resolution.x, resolution.y, refreshRate ?: GLFW_DONT_CARE)
        viewport.update(resolution)
    }
    override fun setWindowed(windowSize: Vector2i) {
        val primaryMonitor = glfwGetPrimaryMonitor()
        val mode = glfwGetVideoMode(primaryMonitor)!!
        val x = ((mode.width().toFloat() - windowSize.x) / 2f).toInt()
        val y = ((mode.height().toFloat() - windowSize.y) / 2f).toInt()

        glfwSetWindowSize(window, windowSize.x, windowSize.y)
        glfwSetWindowMonitor(window, 0, x, y, windowSize.x, windowSize.y, GLFW_DONT_CARE)
        viewport.update(windowSize)
    }

    override fun setVsync(vsync: Boolean) {
        glfwSwapInterval(if (vsync) 1 else 0)
    }

    override fun getPrimaryMonitorResolution(): Vector2i {
        val mode = glfwGetVideoMode(glfwGetPrimaryMonitor())!!
        return Vector2i(mode.width(), mode.height())
    }

    override fun getActualWindowSize(): Vector2i {
        val stack: MemoryStack
        try {
            stack = stackPush()
            val pWidth = stack.mallocInt(1)
            val pHeight = stack.mallocInt(1)
            glfwGetWindowSize(window, pWidth, pHeight)
            return Vector2i(pWidth.get(0), pHeight.get(0))
        } finally {
            stackPop()
        }
    }

    override fun enableKeyboardEvents(enable: Boolean) {
        glfwSetKeyCallback(window, if (enable) ::keyboardEventHandler else null)
    }

    override fun enableTextEvents(enable: Boolean) {
        glfwSetCharCallback(window, if (enable) ::textEventHandler else null)
    }

    override fun enableMouseEvents(enable: Boolean) {
        glfwSetCursorPosCallback(window, if (enable) ::mouseEventHandler else null)
    }

    override fun enableMouseButtonEvents(enable: Boolean) {
        glfwSetMouseButtonCallback(window, if (enable) ::mouseButtonEventHandler else null)
    }

    override fun enableScrollEvents(enable: Boolean) {
        glfwSetScrollCallback(window, if (enable) ::scrollEventHandler else null)
    }

    override fun enableResizeEvents(enable: Boolean) {
        // OpenGL uses pixels, not screen coordinates, so use framebuffer callback for size
        glfwSetFramebufferSizeCallback(window, if(enable) ::resizeEventHandler else null)
    }

    override fun setCursorMode(mode: CursorMode) {
        if (mode == CursorMode.FPS) {
            glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED)
            if (glfwRawMouseMotionSupported()) {
                glfwSetInputMode(window, GLFW_RAW_MOUSE_MOTION, GLFW_TRUE);
            }
        } else {
            glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL)
        }
    }

    override fun swapBuffers() {
        glfwSwapBuffers(window)
    }

    override fun pollEvents() {
        glfwPollEvents()
    }

    override fun getNextEvent() : Event? {
        return events.getNextEvent()
    }

    override fun close() {
        glfwSetWindowShouldClose(window, true)
    }

    override fun shouldClose(): Boolean {
        return glfwWindowShouldClose(window)
    }

    private fun keyboardEventHandler(window: Long, keyCode: Int, scanCode: Int, actionCode: Int, modifierCode: Int) {
        events.pushEvent(KeyEvent.valueOf(keyCode, scanCode, actionCode, modifierCode))
    }

    private fun textEventHandler(window: Long, codePoint: Int) {
        events.pushEvent(TextEvent.valueOf(codePoint))
    }

    private fun mouseEventHandler(window: Long, x: Double, y: Double) {
        events.pushMouseEvent(viewport, MouseEvent.valueOf(x, y))
    }

    private fun mouseButtonEventHandler(window: Long, buttonCode: Int, actionCode: Int, modifierCode: Int) {
        events.pushEvent(MouseButtonEvent.valueOf(buttonCode, actionCode, modifierCode))
    }

    private fun scrollEventHandler(window: Long, x: Double, y: Double) {
        events.pushEvent(ScrollEvent.valueOf(x, y))
    }

    private fun resizeEventHandler(window: Long, x: Int, y: Int) {
        events.pushEvent(ResizeEvent.valueOf(x, y))
    }

    companion object {
        private fun initializeGlfwWindow(title: String, fullScreen: Boolean): Long {
            // Setup an error callback. The default implementation
            // will print the error message in System.err.
            GLFWErrorCallback.createPrint(System.err).set()

            // Initialize GLFW. Most GLFW functions will not work before doing this.
            check(glfwInit()) { "Unable to initialize GLFW" }

            // Configure GLFW
            glfwDefaultWindowHints() // optional, the current window hints are already the default
            glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE) // the window will stay hidden after creation
            glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE) // the window will not be manually resizable

            // Get the monitor to use
            val monitor = if (fullScreen) glfwGetPrimaryMonitor() else 0

            // Create the window
            val window = glfwCreateWindow(1024, 768, title, monitor, 0)
            check(window != 0L) { "Unable to create window" }

            // Make the OpenGL context current
            glfwMakeContextCurrent(window)

            // Make the window visible
            glfwShowWindow(window)

            // This line is critical for LWJGL's interoperation with GLFW's
            // OpenGL context, or any context that is managed externally.
            // LWJGL detects the context that is current in the current thread,
            // creates the GLCapabilities instance and makes the OpenGL
            // bindings available for use.
            GL.createCapabilities()

            // Return the window
            return window
        }
    }
}