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

import dev.misasi.giancarlo.drawing.Rgba8
import dev.misasi.giancarlo.events.SystemClock
import dev.misasi.giancarlo.events.input.gestures.detector.PanDetector
import dev.misasi.giancarlo.events.input.gestures.detector.PinchDetector
import dev.misasi.giancarlo.events.input.gestures.detector.SingleTapDetector
import dev.misasi.giancarlo.math.Vector2f
import dev.misasi.giancarlo.opengl.LwjglGlfwDisplayContext
import dev.misasi.giancarlo.opengl.LwjglOpenGl

fun main() {
    println("Hello")

    val clock = SystemClock()
    val gestureDetectors = setOf(
        PanDetector(),
        SingleTapDetector(),
        PinchDetector()
    )

    val context = LwjglGlfwDisplayContext(
        gestureDetectors,
        "title",
        Vector2f(600f, 400f),
        Vector2f(600f, 400f),
        false,
        false,
        60
    )

    val gl = LwjglOpenGl()
    gl.setClearColor(Rgba8.BLACK)
    while (!context.shouldClose()) {
        gl.clear()
        context.swapBuffers()
        context.pollEvents()
    }
}