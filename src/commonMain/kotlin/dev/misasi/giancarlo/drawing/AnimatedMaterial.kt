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

import dev.misasi.giancarlo.math.Aabb
import dev.misasi.giancarlo.math.Vector2i
import dev.misasi.giancarlo.system.TimeAccumulator

class AnimatedMaterial(
    val animationName: String,
    val frames: List<StaticMaterial>,
    val frameDurationMs: Int,
) : Material {
    data class Spec(val animationName: String, val frames: List<StaticMaterial>, val frameDurationMs: Int)

    constructor(spec: Spec) : this(spec.animationName, spec.frames, spec.frameDurationMs)

    private val accumulator = TimeAccumulator()
    private var index: Int = 0

    override val materialName: String
        get() = currentFrame().materialName

    override val textureName: String
        get() = currentFrame().textureName

    override val coordinates: Aabb
        get() = currentFrame().coordinates

    override val size: Vector2i
        get() = currentFrame().size

    fun update(elapsedMillis: Long) {
        accumulator.update(elapsedMillis)
        if (accumulator.hasElapsed(frameDurationMs.toLong())) {
            accumulator.reset()
            index = (index + 1) % frames.size
        }
    }

    fun restart() {
        accumulator.reset()
        index = 0
    }

    private fun currentFrame(): Material =
        frames[index]
}