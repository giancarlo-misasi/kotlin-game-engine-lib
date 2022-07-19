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

package dev.misasi.giancarlo.drawing

import dev.misasi.giancarlo.math.Point4f
import dev.misasi.giancarlo.opengl.Texture
import dev.misasi.giancarlo.system.TimeAccumulator

class AnimatedMaterial (
    private val materialSet: MaterialSet
) : Material {
    private val accumulator = TimeAccumulator()
    private var index: Int = 0

    override fun name(): String = currentFrame().name()
    override fun texture(): Texture = currentFrame().texture()
    override fun coordinates(): Point4f = currentFrame().coordinates()
    private fun currentFrame() : Material = materialSet.frames[index]

    fun update(elapsedMillis: Long) {
        accumulator.update(elapsedMillis)
        if (accumulator.hasElapsed(materialSet.frameDurationMillis.toLong())) {
            accumulator.reset()
            index = (index + 1) % materialSet.frames.size
        }
    }

    fun restart() {
        accumulator.reset()
        index = 0
    }
}