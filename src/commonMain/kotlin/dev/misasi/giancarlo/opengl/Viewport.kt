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

import dev.misasi.giancarlo.math.Matrix4f
import dev.misasi.giancarlo.math.Vector2f
import dev.misasi.giancarlo.math.Vector3f

class Viewport(gl: OpenGl, private val targetResolution: Vector2f, private val actualScreenSize: Vector2f) {
    private val adjustedScreenSize: Vector2f = calculateAdjustedScreenSize(targetResolution, actualScreenSize)
    private val offset: Vector2f = actualScreenSize.minus(adjustedScreenSize).scale(0.5f)
    private val scale: Vector2f = adjustedScreenSize.divide(targetResolution)

    init {
        gl.setViewport(0, 0, actualScreenSize.x.toInt(), actualScreenSize.y.toInt())
        gl.setScissor(offset.x.toInt(), offset.y.toInt(), adjustedScreenSize.x.toInt(), adjustedScreenSize.y.toInt())
    }

    fun withinBounds(point: Vector2f): Boolean {
        return point.withinBounds(targetResolution)
    }

    fun adjustToBounds(point: Vector2f): Vector2f {
        return point.minus(offset).divide(scale)
    }

    fun getModelViewProjection(camera: Camera): Matrix4f {
        val zoom = 1f / camera.zoom
        val model = Matrix4f.scale(scale)
        val adjusted = camera.position.minus(offset.scale(zoom))
        val view = Matrix4f.lookAt(
            Vector3f(adjusted, 0.5f),
            Vector3f(adjusted, 0.0f),
            Vector3f(0.0f, 1.0f, 0.0f)
        )
        val projection = Matrix4f.ortho(actualScreenSize.scale(zoom))
        return projection.multiply(view).multiply(model).transpose
    }

    companion object {
        private fun calculateAdjustedScreenSize(targetResolution: Vector2f, actualScreenSize: Vector2f): Vector2f {
            val requiredScreenHeight = actualScreenSize.x / targetResolution.aspectRatio;
            return if (requiredScreenHeight > actualScreenSize.y) {
                Vector2f(actualScreenSize.x * targetResolution.aspectRatio, actualScreenSize.y);
            } else {
                Vector2f(actualScreenSize.x, requiredScreenHeight);
            }
        }
    }
}
