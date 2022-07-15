package dev.misasi.giancarlo.opengl

class FrameBuffer (private val gl: OpenGl) {
    private val handle = gl.createFrameBuffer()

    var attachedTexture: Texture? = null
        private set

    fun bind(): FrameBuffer {
        if (boundHandle != handle) {
            gl.bindFrameBuffer(handle)
            boundHandle = handle
        }
        return this
    }

    fun attach(texture: Texture) {
        bind()
        texture.attach()
        attachedTexture = texture
        unbind(gl)
    }

    fun delete() {
        gl.deleteFrameBuffer(handle)
    }

    companion object {
        private var boundHandle = 0

        fun unbind(gl: OpenGl) {
            if (boundHandle != 0) {
                gl.bindFrameBuffer(0)
                boundHandle = 0
            }
        }
    }
}