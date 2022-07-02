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

import dev.misasi.giancarlo.math.Point4
import dev.misasi.giancarlo.math.Vector2f
import dev.misasi.giancarlo.system.Timer

class AnimatedMaterial (
    private val materialSet: MaterialSet
) : Material {
    private val timer = Timer()
    private var index: Int = 0

    override fun name(): String = currentFrame().name()
    override fun textureHandle(): Int = currentFrame().textureHandle()
    override fun coordinates(): Point4 = currentFrame().coordinates()
    override fun size(): Vector2f = currentFrame().size()
    private fun currentFrame() : Material = materialSet.frames[index]

    fun update(elapsedMillis: Int) {
        timer.update(elapsedMillis)
        if (timer.isComplete(materialSet.frameDurationMillis)) {
            timer.restart()
            index = (index + 1) % materialSet.frames.size
        }
    }

    fun restart() {
        timer.restart()
        index = 0
    }
}