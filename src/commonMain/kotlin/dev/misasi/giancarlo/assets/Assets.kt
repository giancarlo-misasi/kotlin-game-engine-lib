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

package dev.misasi.giancarlo.assets

import dev.misasi.giancarlo.assets.caches.AnimationCache
import dev.misasi.giancarlo.assets.caches.BitmapCache
import dev.misasi.giancarlo.assets.caches.FontCache
import dev.misasi.giancarlo.assets.caches.MaterialCache
import dev.misasi.giancarlo.assets.caches.ShaderCache
import dev.misasi.giancarlo.assets.caches.SoundCache
import dev.misasi.giancarlo.assets.caches.TextureCache
import dev.misasi.giancarlo.drawing.AnimatedMaterial
import dev.misasi.giancarlo.drawing.Bitmap
import dev.misasi.giancarlo.drawing.BitmapFont
import dev.misasi.giancarlo.drawing.StaticMaterial
import dev.misasi.giancarlo.openal.OpenAl
import dev.misasi.giancarlo.openal.PcmSound
import dev.misasi.giancarlo.opengl.OpenGl
import dev.misasi.giancarlo.opengl.Shader
import dev.misasi.giancarlo.opengl.Texture

class Assets(gl: OpenGl, al: OpenAl) {
    private val shaderCache = ShaderCache()
    private val bitmapCache = BitmapCache()
    private val materialCache = MaterialCache(bitmapCache)
    private val fontCache = FontCache(bitmapCache, materialCache)
    private val animationCache = AnimationCache(materialCache)
    private val soundCache = SoundCache(al)
    private val textureCache = TextureCache(gl, bitmapCache)

    fun shaders() = shaderCache.keys()
    fun bitmaps() = bitmapCache.keys()
    fun materials() = materialCache.keys()
    fun fonts() = fontCache.keys()
    fun animations() = animationCache.keys()
    fun sounds() = soundCache.keys()
    fun textures() = textureCache.keys()

    fun shader(key: String): Shader.Spec = shaderCache.get(key)
    fun bitmap(key: String): Bitmap = bitmapCache.get(key)
    fun material(key: String): StaticMaterial = materialCache.get(key)
    fun fonts(key: String): BitmapFont = fontCache.get(key)
    fun animation(key: String): AnimatedMaterial.Spec = animationCache.get(key)
    fun sound(key: String): PcmSound = soundCache.get(key)
    fun texture(key: String): Texture = textureCache.get(key)

    fun put(shader: Shader.Spec) = shaderCache.put(shader.name, shader)
    fun put(bitmap: Bitmap) = bitmapCache.put(bitmap.name, bitmap)
    fun put(material: StaticMaterial) = materialCache.put(material.materialName, material)
    fun put(font: BitmapFont) = fontCache.put(font.fontName, font)
    fun put(animation: AnimatedMaterial.Spec) = animationCache.put(animation.animationName, animation)
    fun put(sound: PcmSound) = soundCache.put(sound.name, sound)
    fun put(texture: Texture) = textureCache.put(texture.name, texture)
}