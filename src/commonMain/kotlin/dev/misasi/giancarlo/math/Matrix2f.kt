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

package dev.misasi.giancarlo.math

import dev.misasi.giancarlo.math.Reflection.Companion.toVector2f
import dev.misasi.giancarlo.math.Rotation.Companion.toVector2f

class Matrix2f private constructor(val data: FloatArray) {

    companion object Factory {
        val zero = Matrix2f(floatArrayOf(
            0f, 0f,
            0f, 0f
        ))

        val identity = Matrix2f(floatArrayOf(
            1f, 0f,
            0f, 1f
        ))

        fun scale(scale: Vector2f) : Matrix2f {
            val result = identity.data.copyOf()
            result[0] = scale.x
            result[5] = scale.y
            return Matrix2f(result)
        }

        fun reflect(reflection: Reflection?): Matrix2f {
            return scale(reflection.toVector2f())
        }

        fun rotate(rotation: Rotation?): Matrix2f {
            val result = identity.data.copyOf()
            val (cosTheta, sinTheta) = rotation.toVector2f()
            result[0] = cosTheta
            result[1] = -sinTheta
            result[2] = sinTheta
            result[3] = cosTheta
            return Matrix2f(result)
        }

        fun linearTransform(linearTransform: LinearTransform): Matrix2f {
            val scaleReflect = linearTransform.scale.times(linearTransform.reflection.toVector2f())
            val (cosTheta, sinTheta) = linearTransform.rotation.toVector2f()
            val result = identity.data.copyOf()
            result[0] = scaleReflect.x * cosTheta
            result[1] = -scaleReflect.y * sinTheta
            result[2] = scaleReflect.x * sinTheta
            result[3] = scaleReflect.y * cosTheta
            return Matrix2f(result)
        }
    }

    val transpose by lazy {
        Matrix2f(floatArrayOf(
            data[0], data[2],
            data[1], data[3]
        ))
    }

    fun multiply(other: Matrix2f) : Matrix2f {
        return multiply(data, other.data)
    }

    private fun multiply(a: FloatArray, b: FloatArray) : Matrix2f {
        return Matrix2f(floatArrayOf(
            a[0] * b[0] + a[1] * b[2], a[0] * b[1] + a[1] * b[3],
            a[2] * b[0] + a[3] * b[2], a[2] * b[1] + a[3] * b[3]
        ))
    }
}