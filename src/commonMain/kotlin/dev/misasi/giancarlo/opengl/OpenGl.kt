package dev.misasi.giancarlo.opengl

import dev.misasi.giancarlo.drawing.Rgba8
import dev.misasi.giancarlo.memory.DirectByteBuffer

interface OpenGl {

    // General
    fun enable(target: Int)

    // Program
    fun createProgram(): Int
    fun linkProgram(program: Int): String?
    fun bindProgram(program: Int): Int

    // Shader
    fun createShader(type: Shader.Type): Int
    fun compileShader(shader: Int, source: String): String?
    fun attachShader(program: Int, shader: Int)
    fun detachShader(program: Int, shader: Int)
    fun deleteShader(shader: Int)

    // Uniforms
    fun getUniformLocation(program: Int, name: String): Int
    fun setUniform(program: Int, uniform: Int, value: Any)

    // Attributes
    fun getAttributeLocation(program: Int, name: String): Int
    fun enableVertexAttributeArray(attribute: Int)
    fun setVertexAttributePointerFV(attribute: Int, opts: Attribute)

    // Vertex buffers
    fun createVbo(usage: VertexBuffer.Usage, maxBytes: Int): Int
    fun bindVbo(vbo: Int): Int
    fun updateVboData(vbo: Int, data: DirectByteBuffer, byteOffset: Int = 0)

    // Textures
    fun createTexture2d(width: Int, height: Int, data: DirectByteBuffer): Int
    fun bindTexture2d(texture: Int): Int
    fun setActiveTextureUnit(target: Int)

    // Viewport
    fun setViewport(x: Int, y: Int, width: Int, height: Int)
    fun setScissor(x: Int, y: Int, width: Int, height: Int)

    // Draw
    fun drawTriangles(triangleOffset: Int, triangleCount: Int)
    fun setClearColor(color: Rgba8)
    fun clear()
}