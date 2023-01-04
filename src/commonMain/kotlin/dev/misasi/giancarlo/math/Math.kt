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

import kotlin.math.pow

const val HALF_PI: Double = Math.PI / 2.0;
const val TWO_PI: Double = 2.0 * Math.PI;

fun fastFloor(x: Float): Int {
    val xi = x.toInt()
    return if (x < xi) xi - 1 else xi
}

//fun convertRange(oldMin: Float, oldMax: Float, newMin: Float, newMax: Float, value: Float): Float {
//    val v: Float = if (value < oldMin) {
//        oldMin
//    } else if (value > oldMax) {
//        oldMax
//    } else {
//        value
//    }
//    return (((v - oldMin) * (newMax - newMin)) / (oldMax - oldMin)) + newMin
//}

fun constrainValue(min: Float, max: Float, value: Float): Float {
    return if (value < min) {
        min
    } else if (value > max) {
        max
    } else {
        value
    }
}

fun sin(amplitude: Double, period: Double, horizontal: Double, vertical: Double, x: Long): Double {
    return amplitude * kotlin.math.sin(period * x - horizontal) + vertical
}

fun Float.powList(iterations: Int): List<Float> {
    var v = this
    return listOf(1f) + (1 until iterations).map {
        val result = v
        v *= v
        result
    }
}