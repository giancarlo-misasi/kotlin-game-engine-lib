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

package dev.misasi.giancarlo.ux.effects

import kotlin.math.sin

class DayNight private constructor(val alpha: Float, val colorMix: Float, val am: Boolean) {
    companion object {
        private const val HALF_PI: Double = Math.PI / 2.0;
        private const val TWO_PI: Double = 2.0 * Math.PI;
        private const val LENGTH_OF_DAY_MS: Int = 24 * 60 * 60 * 1000;
        private const val DAY_PERIOD: Double = TWO_PI / LENGTH_OF_DAY_MS;
        private const val HALF_DAY_PERIOD = 2.0 * DAY_PERIOD;

        fun calculate(timeSinceStartMs: Int): DayNight {
            // we use 0.45 and 0.55 so that we convert the vertical range of sin from -1,1 to .1,1
            // this keeps a small level of transparency at all times of day
            val alpha = sin(0.45, DAY_PERIOD, HALF_PI, 0.55, timeSinceStartMs)
            val colorMix = sin(0.45, HALF_DAY_PERIOD, HALF_PI, 0.55, timeSinceStartMs)
            val am = sin(0.45, DAY_PERIOD, HALF_PI, 0.55, timeSinceStartMs + 1) >= alpha;
            return DayNight(alpha.toFloat(), colorMix.toFloat(), am)
        }

        private fun sin(amplitude: Double, period: Double, horizontal: Double, vertical: Double, x: Int): Double {
            return amplitude * sin(period * x - horizontal) + vertical
        }
    }
}