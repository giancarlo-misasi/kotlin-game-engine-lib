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
import dev.misasi.giancarlo.events.input.gestures.detector.GestureDetector
import dev.misasi.giancarlo.math.Vector2f
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryStack.stackPop
import org.lwjgl.system.MemoryStack.stackPush

class LwjglGlfwDisplayContext(
    detectors: Set<GestureDetector>,
    override var title: String,
    override var targetResolution: Vector2f,
    override var windowSize: Vector2f,
    override var fullScreen: Boolean,
    override var vsync: Boolean,
    override var refreshRate: Int
) : DisplayContext {
    private val window: Long
    private val events: EventQueue = EventQueue(detectors)

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

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window) { window, key, scancode, action, mods ->
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(window, true)
            }

            // TODO Add key events, mouse events and push events into the queue here
        }

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
        glfwSetWindowMonitor(window, monitor, x.toInt(), y.toInt(), windowSize.x.toInt(), windowSize.y.toInt(), refreshRate)
        glfwSwapInterval(if (vsync) 1 else 0)
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
}