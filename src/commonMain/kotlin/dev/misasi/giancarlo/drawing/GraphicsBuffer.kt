package dev.misasi.giancarlo.drawing

import dev.misasi.giancarlo.math.Point4
import dev.misasi.giancarlo.math.Rotation
import dev.misasi.giancarlo.math.Rotation.None
import dev.misasi.giancarlo.math.Vector2f
import dev.misasi.giancarlo.memory.DirectByteBuffer

class GraphicsBuffer  (
    maxEntities: Int
) {
    val directBuffer: DirectByteBuffer = DirectByteBuffer(maxEntities * ENTITY_SIZE)
    val drawOrders: MutableList<DrawOrder> = mutableListOf()

    fun write(position: Vector2f, material: Material, rotation: Rotation = None, alpha: Float = NO_ALPHA) {
        val drawOrder = drawOrders.lastOrNull()
        if (drawOrder == null || material.textureHandle != drawOrder.textureHandle) {
            drawOrders.add(DrawOrder(material.textureHandle))
        } else {
            drawOrders[drawOrders.lastIndex] = drawOrder.copy(count = drawOrder.count + 1)
        }

        write(
            Point4.create(position, material.size, rotation),
            material.coordinates,
            alpha
        )
    }

    fun write(position: Vector2f, size: Vector2f, color: Rgba8) {
        val drawOrder = drawOrders.lastOrNull()
        if (drawOrder == null || color != drawOrder.color) {
            drawOrders.add(DrawOrder(color))
        } else {
            drawOrders[drawOrders.lastIndex] = drawOrder.copy(count = drawOrder.count + 1)
        }

        write(
            Point4.create(position, size),
            color
        )
    }

    fun clear() {
        directBuffer.clear()
        drawOrders.clear()
    }

    private fun write(xy: Point4, uv: Point4, alpha: Float) {
        write(xy.tl)
        write(uv.tl)
        write(USE_TEXTURE_COORDINATES)
        write(alpha)

        write(xy.bl)
        write(uv.tl.x)
        write(uv.br.y)
        write(USE_TEXTURE_COORDINATES)
        write(alpha)

        write(xy.br)
        write(uv.br)
        write(USE_TEXTURE_COORDINATES)
        write(alpha)

        write(xy.br)
        write(uv.br)
        write(USE_TEXTURE_COORDINATES)
        write(alpha)

        write(xy.tr)
        write(uv.br.x)
        write(uv.tl.y)
        write(USE_TEXTURE_COORDINATES)
        write(alpha)

        write(xy.tl)
        write(uv.tl)
        write(USE_TEXTURE_COORDINATES)
        write(alpha)
    }

    private fun write(xy: Point4, color: Rgba8) {
        write(xy.tl)
        write(color)

        write(xy.bl)
        write(color)

        write(xy.br)
        write(color)

        write(xy.br)
        write(color)

        write(xy.tr)
        write(color)

        write(xy.tl)
        write(color)
    }

    private fun write(xy: Vector2f) {
        write(xy.x)
        write(xy.y)
    }

    private fun write(color: Rgba8) {
        write(color.floatR)
        write(color.floatG)
        write(color.floatB)
        write(color.floatA)
    }

    private fun write(value: Float) {
        directBuffer.putFloat(value)
    }

    companion object {
        // 2 triangles of 3 points each with 6 floats, 144 bytes per rect
        private const val ENTITY_SIZE: Int = 144

        // This will inform the shader that texture coordinates should be used
        // If not provided, the color provided will be used
        private const val USE_TEXTURE_COORDINATES: Float = -1f

        // When rendering materials, allow user to make them
        // transparent (on top of their existing transparency)
        private const val NO_ALPHA: Float = -1f;
    }
}
