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

import dev.misasi.giancarlo.drawing.DrawOrder
import dev.misasi.giancarlo.drawing.GraphicsBuffer

class Program (
    private val gl: OpenGl,
    shaders: List<Shader>,
    uniforms: List<String>,
    attributes: List<Attribute>
) {
    private val programHandle: Int = gl.createProgram()
    private val uniformsMap: Map<String, Int>
    private val attributesMap: Map<Int, Attribute>

    init {
        // Create, compile and attach shaders
        val shaderHandles = shaders.map {
            val shader = gl.createShader(it.type)
            gl.compileShader(shader, it.source)
            gl.attachShader(programHandle, shader)
            shader
        }

        // Link the program
        gl.linkProgram(programHandle)

        // Cleanup the shader resources
        shaderHandles.forEach {
            gl.detachShader(programHandle, it)
            gl.deleteShader(it)
        }

        // Bind the program
        bind()

        // Setup uniforms
        uniformsMap = uniforms.associateWith { gl.getUniformLocation(programHandle, it) }

        // Setup attributes
        attributesMap = attributes.associateBy { gl.getAttributeLocation(programHandle, it.name) }
    }

    fun bind() {
        gl.bindProgram(programHandle)
    }

    fun updateUniforms(uniformValues: Map<String, Any>) {
        uniformValues.forEach {
            gl.setUniform(programHandle, uniformsMap[it.key]!!, it.value)
        }
    }

    fun enableAttributes() {
        attributesMap.forEach {
            gl.enableVertexAttributeArray(it.key)
            gl.setVertexAttributePointer(it.key, it.value)
        }
    }

    fun draw(
        drawOrders: MutableList<DrawOrder>,
        offset: Int = 0
    ) : Int {
        // Draw the tiles (either textures or colors)
        // Tiles are a square which consist of two triangles
        var totalOffset = offset
        drawOrders.forEach {
            // by default, texture unit 0 is bound
            // to enable multi-sampling, would need to adjust this
            // gl.setActiveTextureUnit(0)
            it.textureHandle?.let { t -> gl.bindTexture2d(t) }
            when (it.type) {
                DrawOrder.Type.SQUARE -> gl.drawTriangles(totalOffset, it.numberOfVertex)
                DrawOrder.Type.LINE -> gl.drawLines(totalOffset, it.numberOfVertex)
            }
            totalOffset += it.numberOfVertex
        }

        // Pass out the offset for future draw calls
        return totalOffset
    }
}
