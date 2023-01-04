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

import dev.misasi.giancarlo.opengl.Attribute.Companion.sizeInBytes
import dev.misasi.giancarlo.system.DataType

class AttributeArray(
    private val gl: OpenGl,
    program: Program,
    attributeSpecs: List<Attribute.Spec>,
    vararg buffersToAttach: Buffer
) {
    val totalSizeInBytes = attributeSpecs.sizeInBytes()
    private val handle: Int = gl.createAttributeArray()
    private val attributes: List<Attribute> = initializeAttributes(program, attributeSpecs)

    init {
        bind()
        buffersToAttach.forEach { it.bind() }
        enableAttributes()
        unbind(gl)
    }

    fun bind(): AttributeArray {
        if (boundHandle != handle) {
            gl.bindAttributeArray(handle)
            boundHandle = handle
        }
        return this
    }

    fun delete() {
        gl.deleteAttributeArray(handle)
    }

    private fun initializeAttributes(program: Program, specs: List<Attribute.Spec>): List<Attribute> {
        val attributes = mutableListOf<Attribute>()
        var offset = 0
        for (spec in specs) {
            val handle = program.getAttributeHandle(spec.name)
            attributes.add(Attribute(handle, spec, offset))
            offset += spec.sizeInBytes
        }
        return attributes.toList()
    }

    private fun enableAttributes() {
        attributes.forEach {
            gl.enableVertexAttributeArray(it.attributeHandle)
            if (it.spec.type == DataType.FLOAT || it.spec.normalize) {
                gl.setVertexAttributePointer(it.attributeHandle, it, totalSizeInBytes)
            } else {
                gl.setVertexAttributeIPointer(it.attributeHandle, it, totalSizeInBytes)
            }
        }
    }

    companion object {
        private var boundHandle = -1

        fun unbind(gl: OpenGl) {
            if (boundHandle != 0) {
                gl.bindAttributeArray(0)
                boundHandle = 0
            }
        }
    }
}