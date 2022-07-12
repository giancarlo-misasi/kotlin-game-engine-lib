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

import dev.misasi.giancarlo.opengl.*

data class DrawOrder(
    val program: Program,
    val attributeArray: AttributeArray,
    val shapeType: ShapeType = ShapeType.SQUARE,
    val texture: Texture? = null,
) {
    var count: Int = 1

    fun vertexCount(): Int {
        return when (shapeType) {
            ShapeType.LINE -> 2 * count
            ShapeType.TRIANGLE -> 3 * count
            ShapeType.SQUARE -> 3 * 2 * count
            else -> throw IllegalArgumentException("ShapeType ${shapeType.name} is not supported.")
        }
    }

    companion object {
        fun updateDrawOrder(drawOrders: MutableList<DrawOrder>, program: Program, attributeArray: AttributeArray, material: Material) {
            val current = drawOrders.lastOrNull()
            if (current == null
                || current.program != program
                || current.attributeArray != attributeArray
                || current.texture != material.texture()
            ) {
                drawOrders.add(
                    DrawOrder(
                        program,
                        attributeArray,
                        texture = material.texture()
                    )
                )
            } else {
                current.count++
            }
        }

        fun updateDrawOrder(drawOrders: MutableList<DrawOrder>, program: Program, attributeArray: AttributeArray, shapeType: ShapeType) {
            val current = drawOrders.lastOrNull()
            if (current == null
                || current.program != program
                || current.attributeArray != attributeArray
                || current.shapeType != shapeType
            ) {
                drawOrders.add(
                    DrawOrder(
                        program,
                        attributeArray,
                        shapeType = shapeType
                    )
                )
            } else {
                current.count++
            }
        }

        fun draw(gl: OpenGl, drawOrders: Iterable<DrawOrder>) {
            var vertexOffset = 0
            drawOrders.forEach {
                // Make sure correct state is bound correctly
                it.program.bind()
                it.attributeArray.bind()
                it.texture?.bind()

                // Draw the vertex in the bound buffer
                val vertexCount = it.vertexCount()
                gl.draw(it.shapeType, vertexOffset, vertexCount)
                vertexOffset += vertexCount
            }

            // Unbind attributes to avoid state modification
            AttributeArray.unbind(gl)
        }

        fun drawIndexed(gl: OpenGl, indexType: DataType, drawOrders: Iterable<DrawOrder>) {
            var vertexOffset = 0
            drawOrders.forEach {
                // Make sure correct state is bound correctly
                it.program.bind()
                it.attributeArray.bind()
                it.texture?.bind()

                // Draw the vertex in the bound buffer
                val vertexCount = it.vertexCount()
                gl.drawIndexed(it.shapeType, indexType.size * vertexOffset, vertexCount, indexType)
                vertexOffset += vertexCount
            }

            // Unbind attributes to avoid state modification
            AttributeArray.unbind(gl)
        }
    }
}