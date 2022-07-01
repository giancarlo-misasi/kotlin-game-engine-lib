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

package dev.misasi.giancarlo.assets.constants

import dev.misasi.giancarlo.opengl.Shader

/**
 * Use [dev.misasi.giancarlo.assets.generators.ShaderGenerator] to regenerate the constants
 * in this file.
 */
class Shaders {
    companion object {
        fun initGraphicsBuffer2dShaders(): List<Shader.Spec> {
            return listOf(
                Shader.Spec(Shader.Type.VERTEX, "#version 100\nprecision highp float;uniform mat4 uMvp;attribute vec2 aXy;attribute vec3 aUvOrRgb;attribute float aAlpha;varying vec3 vUvOrRgb;varying float vAlpha;void main(){gl_Position = uMvp * vec4(aXy, 0.0, 1.0);vUvOrRgb = aUvOrRgb;vAlpha = aAlpha;}"),
                Shader.Spec(Shader.Type.FRAGMENT, "#version 100\nprecision highp float;uniform sampler2D uTexture0;uniform sampler2D uTexture1;uniform sampler2D uTexture2;uniform sampler2D uTexture3;uniform int uEffect;varying vec3 vUvOrRgb;varying float vAlpha;vec3 convert_sepia(vec3 v) {return vec3((v.r * .393) + (v.g *.769) + (v.b * .189), (v.r * .349) + (v.g *.686) + (v.b * .168), (v.r * .272) + (v.g *.534) + (v.b * .131));}vec3 convert_retro(vec3 v) {return (1.0 / 255.0) * vec3(140.0, 173.0, 15.0) * convert_sepia(v);}void main() {vec4 c = vec4(vUvOrRgb, 1.0);if (vUvOrRgb.z < 0.0) {c = texture2D(uTexture0, vUvOrRgb.xy);}if (vAlpha >= 0.0) {c.a = vAlpha;}if (uEffect == 1) {c.rgb = convert_retro(c.rgb);}  else if (uEffect == 2) {c.rgb = convert_sepia(c.rgb);}gl_FragColor = c;}")
            )
        }
    }
}