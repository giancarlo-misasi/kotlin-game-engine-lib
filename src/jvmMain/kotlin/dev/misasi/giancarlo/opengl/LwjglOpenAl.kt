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

package dev.misasi.giancarlo.opengl

import dev.misasi.giancarlo.crash
import dev.misasi.giancarlo.math.Vector3f
import dev.misasi.giancarlo.openal.OpenAl
import dev.misasi.giancarlo.openal.PcmSound
import org.lwjgl.openal.AL10.*
import org.lwjgl.openal.ALC10.*
import org.lwjgl.stb.STBVorbis
import org.lwjgl.stb.STBVorbisInfo
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import java.io.Closeable
import java.nio.ByteBuffer
import java.nio.IntBuffer
import java.nio.ShortBuffer

class LwjglOpenAl : OpenAl {

    override fun init() {
        val byteBuffer: ByteBuffer? = null
        val intBuffer: IntBuffer? = null

        val device = alcOpenDevice(byteBuffer)
        val capabilities = org.lwjgl.openal.ALC.createCapabilities(device)
        val context = alcCreateContext(device, intBuffer)
        alcMakeContextCurrent(context)
        org.lwjgl.openal.AL.createCapabilities(capabilities)
    }

    override fun convertOgg(byteArray: ByteArray): PcmSound {
        STBVorbisInfo.malloc().use { info ->
            MemoryStack.stackPush().use { stack ->
                VorbisHandle(byteArray, stack).use { vorbisHandle ->
                    STBVorbis.stb_vorbis_get_info(vorbisHandle.handle, info)
                    val channels = info.channels()
                    val format = if (channels == 1) PcmSound.Format.MONO16 else PcmSound.Format.STEREO16
                    val pcmBuffer = MemoryUtil.memAllocShort(channels * STBVorbis.stb_vorbis_stream_length_in_samples(vorbisHandle.handle))
                    STBVorbis.stb_vorbis_get_samples_short_interleaved(vorbisHandle.handle, channels, pcmBuffer)
                    return PcmSound(info.sample_rate(), format, toArray(pcmBuffer))
                }
            }
        }
    }

    override fun createSoundBuffer(sound: PcmSound): Int {
        val handle = alGenBuffers();
        val format = formatMap[sound.format]!!
        val data = MemoryUtil.memAllocShort(sound.data.size)
        data.put(0, sound.data).position(0)
        alBufferData(handle, format, data, sound.sampleRate)
        return handle
    }

    override fun deleteSoundBuffer(handle: Int) {
        alDeleteBuffers(handle)
    }

    override fun createSoundSource(loop: Boolean, relative: Boolean): Int {
        val handle = alGenSources();
        if (loop) alSourcei(handle, AL_LOOPING, AL_TRUE)
        if (relative) alSourcei(handle, AL_SOURCE_RELATIVE, AL_TRUE)
        return handle
    }

    override fun setSoundSourceBuffer(handle: Int, bufferHandle: Int) {
        stopSoundSource(handle)
        alSourcei(handle, AL_BUFFER, bufferHandle)
    }

    override fun setSoundSourcePosition(handle: Int, position: Vector3f) {
        alSource3f(handle, AL_POSITION, position.x, position.y, position.z)
    }

    override fun setSoundSourceSpeed(handle: Int, speed: Vector3f) {
        alSource3f(handle, AL_VELOCITY, speed.x, speed.y, speed.z)
    }

    override fun playSoundSource(handle: Int) {
        alSourcePlay(handle)
    }

    override fun pauseSoundSource(handle: Int) {
        alSourcePause(handle)
    }

    override fun stopSoundSource(handle: Int) {
        alSourceStop(handle)
    }

    override fun deleteSoundSource(handle: Int) {
        stopSoundSource(handle)
        alDeleteSources(handle)
    }

    override fun setListenerPosition(position: Vector3f) {
        alListener3f(AL_POSITION, position.x, position.y, position.z)
    }

    override fun setListenerSpeed(speed: Vector3f) {
        alListener3f(AL_VELOCITY, speed.x, speed.y, speed.z)
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

    companion object {
        val formatMap = mapOf(
            PcmSound.Format.MONO16 to AL_FORMAT_MONO16,
            PcmSound.Format.STEREO16 to AL_FORMAT_STEREO16
        )

        val errorMap = mapOf(
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

        fun toArray(shortBuffer: ShortBuffer): ShortArray {
            val array = ShortArray(shortBuffer.limit())
            shortBuffer.get(array)
            return array
        }
    }
}