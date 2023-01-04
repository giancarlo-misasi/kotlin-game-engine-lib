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

package dev.misasi.giancarlo.system

import dev.misasi.giancarlo.system.System.Companion.getCurrentTimeMs
import kotlin.math.max
import kotlin.math.min

class Clock {

    private val startMs: Long = getCurrentTimeMs()

    @Volatile
    private var lastUpdateMs = startMs

    fun elapsedSinceStartMs(): Long = getCurrentTimeMs() - startMs
    fun elapsedSinceUpdateMs(maxStepMs: Long): Long = max(0, min(getCurrentTimeMs() - lastUpdateMs, maxStepMs))
    fun update() = update(getCurrentTimeMs())

    @Synchronized
    private fun update(currentTimeMs: Long) {
        this.lastUpdateMs = currentTimeMs
    }
}