/*
 * MIT License
 *
 * Copyright (c) 2023 Giancarlo Misasi
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

package dev.misasi.giancarlo.ux

import dev.misasi.giancarlo.assets.Assets
import dev.misasi.giancarlo.drawing.DrawCommand
import dev.misasi.giancarlo.drawing.DrawOptions
import dev.misasi.giancarlo.drawing.DrawState
import dev.misasi.giancarlo.drawing.Font
import dev.misasi.giancarlo.drawing.Material
import dev.misasi.giancarlo.drawing.Rgb8
import dev.misasi.giancarlo.drawing.Rgba8
import dev.misasi.giancarlo.drawing.TextureCoordinate
import dev.misasi.giancarlo.math.Aabb
import dev.misasi.giancarlo.math.AffineTransform
import dev.misasi.giancarlo.math.Vector2f
import dev.misasi.giancarlo.opengl.DrawBuffer
import dev.misasi.giancarlo.opengl.NioBuffer
import dev.misasi.giancarlo.system.DataType

class AppDrawState (
    private val assets: Assets,
    private val spriteSizeInBytes: Int,
    private val spriteCapacity: Int,
) : DrawState {
    private val directIndexBuffer = indexes(spriteCapacity).let { NioBuffer(indexType, it.size).putInts(it) }
    private val directVertexBuffer = NioBuffer(vertexType, spriteSizeInBytes * spriteCapacity)
    private val commands = ArrayDeque<DrawCommand>(spriteCapacity)
    private var drawOptions = DrawOptions()
    private var spriteCount = 0

    override fun applyOptionsScoped(scopedDrawOptions: DrawOptions, block: (concatDrawOptions: DrawOptions) -> Unit) {
        val old = drawOptions
        drawOptions = drawOptions.concatenate(scopedDrawOptions)
        block(drawOptions)
        drawOptions = old
    }

    override fun putSprite(materialName: String, affine: AffineTransform, color: Rgb8?, alpha: Int?) {
        if (spriteCount >= spriteCapacity) throw IllegalStateException("Sprite capacity overflow.")
        val material = assets.material(materialName)
        val command = DrawCommand(material.textureName, drawOptions)
        if (commands.isEmpty() || commands.last() != command) {
            commands.addLast(command)
        } else {
            commands.last().count++
        }
        putSpriteIntoDirectVertexBuffer(material, affine, color, alpha)
        spriteCount++
    }

    override fun putText(text: String, affine: AffineTransform, font: Font, alpha: Int?) {
        assets.fonts(font.assetName).draw(text, affine, font, alpha, this)
    }

    override fun reset() {
        directVertexBuffer.setIndex(0)
        commands.clear()
        drawOptions = DrawOptions()
        spriteCount = 0
    }

    fun cleanup() {
        directIndexBuffer.cleanup()
        directVertexBuffer.cleanup()
    }

    fun updateIndexes(drawBuffer: DrawBuffer) = drawBuffer.updateIndexes(directIndexBuffer)

    fun updateVertexes(drawBuffer: DrawBuffer) = drawBuffer.updateVertexes(directVertexBuffer)

    fun getCommands(): List<DrawCommand> = commands

    private fun putSpriteIntoDirectVertexBuffer(material: Material, affine: AffineTransform, color: Rgb8?, alpha: Int?) {
        val offset = affine.scale.half() // required since defaultAabb is around origin to support rotation
        val xy = affine.applyLinearTransform(defaultAabb)
        val uv = material.coordinates
        val c = Rgba8(color, alpha)
        // 0 -> useAlpha
        // 1 ->
        // 2-31 -> Unused
        val mask = (bit(color) shl 1) or (bit(alpha))

        directVertexBuffer.putVector2f(xy.tl.plus(offset).plus(affine.translation))
        putTextureCoordinate(uv.tl)
        directVertexBuffer.putRgba8(c)
        directVertexBuffer.putInt(mask)

        directVertexBuffer.putVector2f(xy.bl.plus(offset).plus(affine.translation))
        putTextureCoordinate(uv.bl)
        directVertexBuffer.putRgba8(c)
        directVertexBuffer.putInt(mask)

        directVertexBuffer.putVector2f(xy.br.plus(offset).plus(affine.translation))
        putTextureCoordinate(uv.br)
        directVertexBuffer.putRgba8(c)
        directVertexBuffer.putInt(mask)

        directVertexBuffer.putVector2f(xy.tr.plus(offset).plus(affine.translation))
        putTextureCoordinate(uv.tr)
        directVertexBuffer.putRgba8(c)
        directVertexBuffer.putInt(mask)
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

        private fun indexes(count: Int): List<Int> {
            return (0 until count).map { i ->
                val start = 4 * i
                arrayOf(0, 1, 2, 2, 3, 0).map { j -> start + j }
            }.flatten()
        }

        private fun <T> bit(value: T?) = if(value == null) 0 else 1
    }
}