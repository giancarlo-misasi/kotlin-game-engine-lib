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

data class Camera (
    val position: Vector2f = Vector2f(),
    val zoom: Float = 1f // bigger numbers zoom in, smaller zoom out
) {
    fun mvp(targetResolution: Vector2f): Matrix4f = Companion.mvp(targetResolution, position, Vector2f(zoom, zoom))

    companion object {
        fun mvp(targetResolution: Vector2f, position: Vector2f = Vector2f(), scale: Vector2f = Vector2f(1f, 1f)): Matrix4f {
            val model = Matrix4f.scale(scale)
            val view = Matrix4f.lookAt(
                Vector3f(position, 0.5f),
                Vector3f(position, 0.0f),
                Vector3f(0.0f, 1.0f, 0.0f)
            )
            val projection = Matrix4f.ortho(targetResolution)
            return projection.multiply(view).multiply(model).transpose
        }
    }
}