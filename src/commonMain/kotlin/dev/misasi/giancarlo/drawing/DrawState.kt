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

package dev.misasi.giancarlo.drawing

import dev.misasi.giancarlo.assets.Assets
import dev.misasi.giancarlo.math.Aabb
import dev.misasi.giancarlo.math.AffineTransform
import dev.misasi.giancarlo.math.Vector2f
import dev.misasi.giancarlo.opengl.DrawBuffer
import dev.misasi.giancarlo.opengl.NioBuffer
import dev.misasi.giancarlo.system.DataType

class DrawState(
    private val assets: Assets,
    private val spriteSizeInBytes: Int,
    private val spriteCapacity: Int,
) {
    private val directIndexBuffer = generateIndexes(spriteCapacity).let { NioBuffer(indexType, it.size).putInts(it) }
    private val directVertexBuffer = NioBuffer(vertexType, spriteSizeInBytes * spriteCapacity)

    private val affines = ArrayDeque<AffineTransform>()
    private val alphas = ArrayDeque<Float>()
    private val effects = ArrayDeque<Effect>()

    private val commands = ArrayDeque<DrawCommand>(spriteCapacity)
    private var spriteCount = 0

    fun cleanup() {
        directIndexBuffer.cleanup()
        directVertexBuffer.cleanup()
    }

    fun reset() {
        directVertexBuffer.setIndex(0)
        affines.clear()
        alphas.clear()
        effects.clear()
        commands.clear()
        spriteCount = 0
    }

    fun pushAffine(affine: AffineTransform) {
        if (affines.isEmpty()) {
            affines.addLast(affine)
        } else {
            affines.addLast(affines.last().concatenate(affine))
        }
    }

    fun pushAlpha(alpha: Float) {
        if (alphas.isEmpty()) {
            alphas.addLast(alpha)
        } else {
            alphas.addLast(alphas.last() * alpha)
        }
    }

    fun pushEffect(effect: Effect) {
        effects.addLast(effect)
    }

    fun popAffine() = affines.removeLast()
    fun popAlpha() = alphas.removeLast()
    fun popEffect() = effects.removeLast()

    fun putSprite(materialName: String, affine: AffineTransform = AffineTransform(), alpha: Float? = null) {
        if (spriteCount >= spriteCapacity) throw IllegalStateException("Sprite capacity overflow.")
        val material = assets.material(materialName)
        val command = getDrawCommand(material)
        if (commands.isEmpty() || commands.last() != command) {
            commands.addLast(command)
        } else {
            commands.last().count++
        }
        putSpriteIntoDirectVertexBuffer(material, affine, alpha)
        spriteCount++
    }

    fun updateIndexes(drawBuffer: DrawBuffer) = drawBuffer.updateIndexes(directIndexBuffer)

    fun updateVertexes(drawBuffer: DrawBuffer) = drawBuffer.updateVertexes(directVertexBuffer)

    fun getCommands(): List<DrawCommand> = commands

    private fun getDrawCommand(material: Material) = DrawCommand(
        material.textureName,
        affines.lastOrNull(),
        alphas.lastOrNull(),
        effects.lastOrNull()
    )

    private fun putSpriteIntoDirectVertexBuffer(material: Material, affine: AffineTransform, alpha: Float?) {
        val offset = affine.scale.half() // required since defaultAabb is around origin to support rotation
        val xy = affine.applyLinearTransform(defaultAabb)
        val uv = material.coordinates
        val a = Alpha.normalize(alpha)

        directVertexBuffer.putVector2f(xy.tl.plus(offset).plus(affine.translation))
        putTextureCoordinate(uv.tl)
        directVertexBuffer.putFloat(a)

        directVertexBuffer.putVector2f(xy.bl.plus(offset).plus(affine.translation))
        putTextureCoordinate(uv.bl)
        directVertexBuffer.putFloat(a)

        directVertexBuffer.putVector2f(xy.br.plus(offset).plus(affine.translation))
        putTextureCoordinate(uv.br)
        directVertexBuffer.putFloat(a)

        directVertexBuffer.putVector2f(xy.tr.plus(offset).plus(affine.translation))
        putTextureCoordinate(uv.tr)
        directVertexBuffer.putFloat(a)
    }

    private fun putTextureCoordinate(uv: Vector2f) {
        directVertexBuffer.putUShort(TextureCoordinate.normalize(uv.x))
        directVertexBuffer.putUShort(TextureCoordinate.normalize(uv.y))
    }

    companion object {
        private val indexType = DataType.UNSIGNED_INT
        private val vertexType = DataType.BYTE
        private val defaultAabb = Aabb(
            Vector2f(-0.5f, -0.5f),
            Vector2f(0.5f, -0.5f),
            Vector2f(0.5f, 0.5f),
            Vector2f(-0.5f, 0.5f)
        )

        private fun generateIndexes(count: Int): List<Int> {
            return (0 until count).map { i ->
                val start = 4 * i
                arrayOf(0, 1, 2, 2, 3, 0).map { j -> start + j }
            }.flatten()
        }
    }
}