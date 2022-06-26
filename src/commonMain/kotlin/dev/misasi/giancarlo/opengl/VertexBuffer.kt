package dev.misasi.giancarlo.opengl

class VertexBuffer (
    gl: OpenGl,
    val usage: Usage,
    val maxBytes: Int
) {
    val vertexBufferHandle: Int = gl.createVbo(usage, maxBytes);

    enum class Usage {
        Static,
        Dynamic,
        Stream
    }
}
