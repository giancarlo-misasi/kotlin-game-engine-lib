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

package dev.misasi.giancarlo.events

import dev.misasi.giancarlo.getTimeMillis
import kotlin.math.min

class SystemClock {

    @Volatile
    var elapsedMillis = 0

    @Volatile
    private var lastTimeMillis = getTimeMillis()

    @Synchronized
    fun update() {
        elapsedMillis = getElapsedMillis(lastTimeMillis)
        lastTimeMillis += elapsedMillis
    }

    private fun getElapsedMillis(lastTimeMillis: Long) : Int {
        val currentTimeMillis = getTimeMillis()
        val delta = min(currentTimeMillis - lastTimeMillis, MAX_STEP_MILLIS)
        return if (delta == -1L) {
            0
        } else {
            delta.toInt()
        }
    }

    companion object {
        private const val MAX_STEP_MILLIS: Long = 1000
    }
}