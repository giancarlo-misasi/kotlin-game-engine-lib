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
import dev.misasi.giancarlo.system.System.Companion.crash
import org.lwjgl.openal.AL10
import org.lwjgl.openal.ALC10
import org.lwjgl.stb.STBVorbis
import org.lwjgl.stb.STBVorbisInfo
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import java.io.Closeable
import java.nio.ByteBuffer
import java.nio.IntBuffer
import java.nio.ShortBuffer

actual class OpenAl private constructor() {

    actual fun convertOgg(name: String, byteArray: ByteArray): PcmSound {
        STBVorbisInfo.malloc().use { info ->
            MemoryStack.stackPush().use { stack ->
                VorbisHandle(byteArray, stack).use { vorbisHandle ->
                    STBVorbis.stb_vorbis_get_info(vorbisHandle.handle, info)
                    val channels = info.channels()
                    val format = if (channels == 1) PcmSound.Format.MONO16 else PcmSound.Format.STEREO16
                    val pcmBuffer =
                        MemoryUtil.memAllocShort(channels * STBVorbis.stb_vorbis_stream_length_in_samples(vorbisHandle.handle))
                    STBVorbis.stb_vorbis_get_samples_short_interleaved(vorbisHandle.handle, channels, pcmBuffer)
                    return PcmSound(name, info.sample_rate(), format, toArray(pcmBuffer))
                }
            }
        }
    }

    actual fun createSoundBuffer(sound: PcmSound): Int {
        val handle = AL10.alGenBuffers();
        val format = formatMap[sound.format]!!
        val data = MemoryUtil.memAllocShort(sound.data.size)
        data.put(0, sound.data).position(0)
        AL10.alBufferData(handle, format, data, sound.sampleRate)
        return handle
    }

    actual fun deleteSoundBuffer(handle: Int) {
        AL10.alDeleteBuffers(handle)
    }

    actual fun createSoundSource(loop: Boolean, relative: Boolean): Int {
        val handle = AL10.alGenSources();
        if (loop) AL10.alSourcei(handle, AL10.AL_LOOPING, AL10.AL_TRUE)
        if (relative) AL10.alSourcei(handle, AL10.AL_SOURCE_RELATIVE, AL10.AL_TRUE)
        return handle
    }

    actual fun setSoundSourceBuffer(handle: Int, bufferHandle: Int) {
        stopSoundSource(handle)
        AL10.alSourcei(handle, AL10.AL_BUFFER, bufferHandle)
    }

    actual fun setSoundSourcePosition(handle: Int, position: Vector3f) {
        AL10.alSource3f(handle, AL10.AL_POSITION, position.x, position.y, position.z)
    }

    actual fun setSoundSourceSpeed(handle: Int, speed: Vector3f) {
        AL10.alSource3f(handle, AL10.AL_VELOCITY, speed.x, speed.y, speed.z)
    }

    actual fun playSoundSource(handle: Int) {
        AL10.alSourcePlay(handle)
    }

    actual fun pauseSoundSource(handle: Int) {
        AL10.alSourcePause(handle)
    }

    actual fun stopSoundSource(handle: Int) {
        AL10.alSourceStop(handle)
    }

    actual fun deleteSoundSource(handle: Int) {
        stopSoundSource(handle)
        AL10.alDeleteSources(handle)
    }

    actual fun setListenerPosition(position: Vector3f) {
        AL10.alListener3f(AL10.AL_POSITION, position.x, position.y, position.z)
    }

    actual fun setListenerSpeed(speed: Vector3f) {
        AL10.alListener3f(AL10.AL_VELOCITY, speed.x, speed.y, speed.z)
    }

    private class VorbisHandle(bytes: ByteArray, stack: MemoryStack) : Closeable {
        val handle: Long

        init {
            val error = stack.mallocInt(1)
            val data = ByteBuffer.allocateDirect(bytes.size).put(0, bytes).position(0)
            handle = STBVorbis.stb_vorbis_open_memory(data, error, null)
            if (handle == 0L) {
                crash("Failed to decode ogg, error = ${errorMap[error[0]] ?: "Unknown error"}")
            }
        }

        override fun close() {
            STBVorbis.stb_vorbis_close(handle)
        }
    }

    actual companion object {
        actual val al: OpenAl get() = getInstance()

        private val formatMap = mapOf(
            PcmSound.Format.MONO16 to AL10.AL_FORMAT_MONO16,
            PcmSound.Format.STEREO16 to AL10.AL_FORMAT_STEREO16
        )

        private val errorMap = mapOf(
            30 to "VORBIS missing capture pattern",
            31 to "VORBIS invalid stream structure version",
            32 to "VORBIS continued packet flag invalid",
            33 to "VORBIS incorrect stream serial number",
            34 to "VORBIS invalid first page",
            35 to "VORBIS bad packet type",
            36 to "VORBIS cant find last page",
            37 to "VORBIS seek failed",
            38 to "VORBIS ogg skeleton not supported"
        )

        private fun toArray(shortBuffer: ShortBuffer): ShortArray {
            val array = ShortArray(shortBuffer.limit())
            shortBuffer.get(array)
            return array
        }

        @Volatile
        private var INSTANCE: OpenAl? = null

        private fun init(): OpenAl {
            val byteBuffer: ByteBuffer? = null
            val intBuffer: IntBuffer? = null
            val device = ALC10.alcOpenDevice(byteBuffer)
            val capabilities = org.lwjgl.openal.ALC.createCapabilities(device)
            val context = ALC10.alcCreateContext(device, intBuffer)
            ALC10.alcMakeContextCurrent(context)
            org.lwjgl.openal.AL.createCapabilities(capabilities)
            return OpenAl()
        }

        private fun getInstance(): OpenAl =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: init().also { INSTANCE = it }
            }
    }
}