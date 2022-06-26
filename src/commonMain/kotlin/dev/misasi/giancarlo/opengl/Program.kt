package dev.misasi.giancarlo.opengl

import dev.misasi.giancarlo.crash
import dev.misasi.giancarlo.drawing.DrawOrder

class Program (
    gl: OpenGl,
    shaders: List<Shader>,
    uniforms: List<String>,
    attributes: List<Attribute>
) {
    val programHandle: Int
    private val uniformsMap: Map<String, Int>
    private val attributesMap: Map<Int, Attribute>

    init {
        // Create the program
        programHandle = gl.createProgram()

        // Create, compile and attach shaders
        val shaderHandles = shaders.map {
            val shader = gl.createShader(it.type)
            val status = gl.compileShader(shader, it.source)
            if (status != null) {
                crash("Failed to compile shader. Status=$status, Source=${it.source}");
            }
            gl.attachShader(programHandle, shader)
            shader
        }

        // Link the program
        val status = gl.linkProgram(programHandle)
        if (status != null) {
            crash("Failed to link program: $status.");
        }

        // Cleanup the shader resources
        shaderHandles.forEach {
            gl.detachShader(programHandle, it)
            gl.deleteShader(it)
        }

        // Bind the program
        gl.bindProgram(programHandle)

        // Setup uniforms
        uniformsMap = uniforms.associateWith { gl.getUniformLocation(programHandle, it) }

        // Setup attributes
        attributesMap = attributes.associateBy { gl.getAttributeLocation(programHandle, it.name) }
    }

    fun draw(
        gl: OpenGl,
        uniformValues: Map<String, Any>,
        drawOrders: List<DrawOrder>,
        vertexBuffer: VertexBuffer,
        triangleOffset: Int = 0
    ) : Int {
        // Bind the program
        gl.bindProgram(programHandle)

        // Setup uniform values
        uniformValues.forEach {
            gl.setUniform(programHandle, uniformsMap[it.key]!!, it.value)
        }

        // Bind the buffer we will associate attributes to
        gl.bindVbo(vertexBuffer.vertexBufferHandle)

        // Enable the attributes
        attributesMap.forEach {
            gl.enableVertexAttributeArray(it.key)
            gl.setVertexAttributePointerFV(it.key, it.value)
        }

        // TODO: Add mapping between texture handles and units
        gl.setActiveTextureUnit(0)

        // Draw the tiles (either textures or colored)
        // Tiles are a square which consist of two triangles
        var totalTriangleOffset = triangleOffset
        drawOrders.forEach {
            it.textureHandle?.let { t -> gl.bindTexture2d(t) }
            gl.drawTriangles(totalTriangleOffset, it.numberOfTriangles)
            totalTriangleOffset += it.numberOfTriangles
        }

        // Pass out the offset for future draw calls
        return totalTriangleOffset
    }
}
