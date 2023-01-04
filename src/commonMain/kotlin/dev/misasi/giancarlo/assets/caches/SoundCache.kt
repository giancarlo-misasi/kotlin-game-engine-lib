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

package dev.misasi.giancarlo.assets.caches

import dev.misasi.giancarlo.collections.LruMap
import dev.misasi.giancarlo.openal.OpenAl
import dev.misasi.giancarlo.openal.PcmSound
import dev.misasi.giancarlo.system.System.Companion.getResourceAsBytes

class SoundCache(private val al: OpenAl) : Cache<PcmSound> {
    private val sounds = LruMap<String, PcmSound>(100)

    override fun get(name: String): PcmSound {
        return sounds.getOrPut(name) { pcmSound(name, al) }
    }

    override fun put(name: String, value: PcmSound) {
        sounds[name] = value
    }

    companion object {
        private const val PATH = "/sounds/"
        private const val EXTENSION = "ogg"

        private fun pcmSound(name: String, al: OpenAl) = al.convertOgg(name, oggBytes(name))

        private fun oggBytes(name: String): ByteArray {
            val path = "$PATH/$name.$EXTENSION"
            return getResourceAsBytes(path)
        }
    }
}