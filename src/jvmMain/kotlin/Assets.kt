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

//import android.content.Context
//import android.util.DisplayMetrics
//import android.view.WindowManager
//import dev.misasi.giancarlo.crash
//import dev.misasi.giancarlo.drawing.Atlas
//import dev.misasi.giancarlo.listFiles
//import dev.misasi.giancarlo.math.Vector2f
//import dev.misasi.giancarlo.opengl.Program
//import dev.misasi.giancarlo.opengl.VertexBuffer
//import dev.misasi.giancarlo.opengl.Viewport
//
//class Assets (
//    private val context: Context
//) {
//    private lateinit var programs: Map<String, Program>
//    private lateinit var atlases: Map<String, Atlas>
//    private lateinit var buffers: Map<String, VertexBuffer>
//    private var targetResolution: Vector2f = Vector2f(1920f, 1080f)
//    // TODO: Read in all assets like textures, fonts and languages
//
//    var viewport: Viewport = Viewport(targetResolution, targetResolution)
//        private set
//
//    fun initialize() {
//        programs = listFiles(context, "programs")
//            .map { Program(context, it) }
//            .map { it.name to it }
//            .toMap()
//        atlases = listFiles(context, "atlases")
//            .map { Atlas(context, it) }
//            .map { it.name to it }
//            .toMap()
//        buffers = listFiles(context, "buffers")
//            .map { VertexBuffer(context, it) }
//            .map { it.name to it }
//            .toMap()
//        viewport = Viewport(targetResolution, getScreenSize(context))
//    }
//
//    fun getProgram(name: String): Program {
//        return programs[name]
//            ?: crash("Shader $name not found.")
//    }
//
//    fun getAtlas(name: String): Atlas {
//        return atlases[name]
//            ?: crash("Atlas $name not found.")
//    }
//
//    fun getBuffer(name: String): VertexBuffer {
//        return buffers[name]
//            ?: crash("Buffer $name not found.")
//    }
//
//    companion object {
//        private fun getScreenSize(context: Context): Vector2f {
//            val windowManager = context.getSystemService(Context.WINDOW_SERVICE)
//                    as WindowManager?
//                ?: crash("Could not find window manager.")
//
//            val metrics = DisplayMetrics()
//            windowManager.defaultDisplay.getMetrics(metrics)
//            return Vector2f(metrics.widthPixels.toFloat(), metrics.heightPixels.toFloat())
//        }
//    }
//}