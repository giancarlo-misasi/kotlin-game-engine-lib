package dev.misasi.giancarlo.opengl

import dev.misasi.giancarlo.crash

data class Attribute (
    val name : String,
    val type: Type,
    val count: Int,
    val strideOffset: Int,
    val totalStride: Int,
    val normalized: Boolean
) {
    enum class Type {
        FLOAT
    }

    init {
        if (count <= 0) {
            crash("Vertex attribute specification must have at least 1 parameter.")
        }
    }

    val size by lazy {
        PARAMETER_SIZE * count
    }

    companion object {
        private const val PARAMETER_SIZE: Int = 4
    }
}
