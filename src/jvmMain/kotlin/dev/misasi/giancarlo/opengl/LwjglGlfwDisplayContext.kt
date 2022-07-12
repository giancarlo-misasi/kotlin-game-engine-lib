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
import dev.misasi.giancarlo.math.Vector2f
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryStack.stackPop
import org.lwjgl.system.MemoryStack.stackPush

class LwjglGlfwDisplayContext(
    override var title: String,
    override var targetResolution: Vector2f,
    override var windowSize: Vector2f,
    override var fullScreen: Boolean,
    override var vsync: Boolean,
    override var refreshRate: Int?,
    private val events: EventQueue
) : DisplayContext {
    private val window: Long
    override val gl: OpenGl
    override val viewport: Viewport

    init {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set()

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        check(glfwInit()) { "Unable to initialize GLFW" }

        // Configure GLFW
        glfwDefaultWindowHints() // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE) // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE) // the window will be resizable

        // Get the primary monitor
        val primaryMonitor = glfwGetPrimaryMonitor()
        val monitor = if (fullScreen) primaryMonitor else 0

        // Create the window
        window = glfwCreateWindow(windowSize.x.toInt(), windowSize.y.toInt(), title, monitor, 0)
        check(window != 0L) { "Unable to create window" }

        // Make the OpenGL context current
        glfwMakeContextCurrent(window)

        // Update the window based on all configuration parameters
        reconfigure()

        // Make the window visible
        glfwShowWindow(window)

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities()

        // Now let's create the OpenGL instance
        gl = LwjglOpenGl()

        // Setup the viewport for events
        val actualScreenSize = if (fullScreen) targetResolution else getActualWindowSize()
        viewport = Viewport(gl, targetResolution, actualScreenSize)
    }

//    override fun getOpenGl() = gl
//    override fun getViewport() = viewport

    override fun getPrimaryMonitorResolution(): Vector2f {
        val mode = glfwGetVideoMode(glfwGetPrimaryMonitor())!!
        return Vector2f(mode.width().toFloat(), mode.height().toFloat())
    }

    override fun getActualWindowSize(): Vector2f {
        val stack: MemoryStack
        try {
            stack = stackPush()
            val pWidth = stack.mallocInt(1)
            val pHeight = stack.mallocInt(1)
            glfwGetWindowSize(window, pWidth, pHeight)
            return Vector2f(pWidth.get(0).toFloat(), pHeight.get(0).toFloat())
        } finally {
            stackPop()
        }
    }

    override fun reconfigure() {
        val primaryMonitor = glfwGetPrimaryMonitor()
        val mode = glfwGetVideoMode(primaryMonitor)!!

        val monitor: Long
        val x: Float
        val y: Float
        if (fullScreen) {
            monitor = primaryMonitor
            x = 0f
            y = 0f
        } else {
            monitor = 0
            x = (mode.width().toFloat() - windowSize.x) / 2f
            y = (mode.height().toFloat() - windowSize.y) / 2f
        }

        glfwSetWindowTitle(window, title)
        glfwSetWindowSize(window, windowSize.x.toInt(), windowSize.y.toInt())
        glfwSetWindowMonitor(window, monitor, x.toInt(), y.toInt(), windowSize.x.toInt(), windowSize.y.toInt(), refreshRate ?: GLFW_DONT_CARE)
        glfwSwapInterval(if (vsync) 1 else 0)
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
        glfwSetWindowSizeCallback(window, if(enable) ::resizeEventHandler else null)
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
}