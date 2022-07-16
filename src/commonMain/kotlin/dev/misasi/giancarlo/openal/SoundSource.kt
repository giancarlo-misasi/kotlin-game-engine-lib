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

package dev.misasi.giancarlo.openal

import dev.misasi.giancarlo.math.Vector3f

class SoundSource (private val al: OpenAl, position: Vector3f = Vector3f(), loop: Boolean = false, relative: Boolean = false) {
    private val handle: Int = al.createSoundSource(loop, relative)

    init {
        setPosition(position)
    }

    fun attach(buffer: SoundBuffer): SoundSource {
        buffer.attach(handle)
        return this
    }

    fun setPosition(position: Vector3f): SoundSource {
        al.setSoundSourcePosition(handle, position)
        return this
    }

    fun setSpeed(speed: Vector3f): SoundSource {
        al.setSoundSourceSpeed(handle, speed)
        return this
    }

    fun play(): SoundSource {
        al.playSoundSource(handle)
        return this
    }

    fun pause(): SoundSource {
        al.pauseSoundSource(handle)
        return this
    }

    fun stop(): SoundSource {
        al.stopSoundSource(handle)
        return this
    }

    fun delete() {
        al.deleteSoundSource(handle)
    }
}