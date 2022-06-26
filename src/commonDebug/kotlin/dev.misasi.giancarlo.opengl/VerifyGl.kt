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

import dev.misasi.giancarlo.crash

inline fun <T> glVerify(gl: OpenGl, glOperation: () -> T): T {
    val result = glOperation()
    val error = gl.getErrorMessage()
    if (error != null) {
        crash(error)
    }
    return result
}

inline fun glVerify(gl: OpenGl, glOperation: () -> Unit) {
    glOperation()
    val error = gl.getErrorMessage()
    if (error != null) {
        crash(error)
    }
}

inline fun glVerifyCreate(gl: OpenGl, crossinline glCreateOperation: () -> Int, tag: () -> String): Int {
    val handle = glVerify<Int>(gl) { glCreateOperation() }
    if (handle == 0) {
        crash("Failed to create ${tag()}.")
    }
    return handle
}

inline fun glVerifyGenerate(gl: OpenGl, crossinline glGenOperation: (IntArray) -> Unit, tag: () -> String): Int {
    val handleArray = IntArray(1)
    glVerify(gl) { glGenOperation(handleArray) }
    if (handleArray[0] == 0) {
        crash("Failed to create ${tag()}.")
    }
    return handleArray[0]
}

inline fun glVerifyStatus(
    gl: OpenGl,
    crossinline glGetStatusOperation: (Int, Int, IntArray) -> Unit,
    handle: Int,
    attribute: Int,
    glGetLogOperation: (Int) -> String
) {
    val status = IntArray(1)
    glVerify(gl) { glGetStatusOperation(handle, attribute, status) }
    if (status[0] == 0) {
        crash(glVerify<String>(gl) { glGetLogOperation(handle) })
    }
}

inline fun glVerifyBound(gl: OpenGl, name: Int, handle: Int, tag: () -> String) {
    val currentHandle = gl.getCurrentHandle(name)
    if (currentHandle != handle) {
        crash("[${tag()}] $currentHandle was bound instead of $handle.")
    }
}