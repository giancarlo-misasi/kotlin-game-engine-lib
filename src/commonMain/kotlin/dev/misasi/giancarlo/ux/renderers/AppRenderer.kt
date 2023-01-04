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

package dev.misasi.giancarlo.ux.renderers
//
//import dev.misasi.giancarlo.math.Aabb
//import dev.misasi.giancarlo.math.Vector2f
//import dev.misasi.giancarlo.ux.AppContext
//import dev.misasi.giancarlo.ux.App
//import dev.misasi.giancarlo.ux.Renderer
//
//class AppRenderer : Renderer {
//
//    override suspend fun load(target: Any, context: AppContext, data: Map<String, Any>) {
//
//    }
//
//    override fun render(target: Any, context: AppContext, data: Map<String, Any>) {
//        if (target !is App) return
////        context.gl.setClearColor(Rgba8.CORN_FLOWER_BLUE)
////        context.gl.setClearColor(Rgba8.BLACK)
//    }
//
//    //    val postProcessingFrameBuffer = FrameBuffer(context.gl)
//
//    //    private val postProcessingProgram = assets.graphics.get()
////        Sprite2dGraphics(context, assets, context.viewport.actualScreenSize)
////    private val postProcessingTexture = Texture(
////        context.gl,
////        "postProcessingTexture",
////        context.viewport.designedResolution,
////        frameBuffer = postProcessingFrameBuffer
////    )
////    private val postProcessingMaterial = StaticMaterial(
////        "postProcessingMaterial",
////        postProcessingTexture.name,
////        postProcessUv
////    )
////    private val postProcessingMesh =
////        Mesh(context.gl, postProcessingProgram, Buffer.Usage.STATIC, 1, postProcessingTexture)
//
////        updatePostProcessingActualScreenSize()
//
////    private fun onDraw() {
////        // Attach the frame buffer to handle scaling and post-processing
////        postProcessingFrameBuffer.bind()
////
////        // Clean the frame buffer to avoid artifacts
////        context.gl.clear()
////
////        // Set the viewport to the target resolution
////        // everything draws assuming target resolution and then this will scale to fit nicely
////        context.gl.setViewport(context.designedResolution)
////
//////        // Set the program
//////        program.bind()
////
////        // Draw the root views
////        // First check if we have transitions, if we do, we will draw those
////        // Otherwise, draw the root view
//////        val transitions = transitionOrchestrator.activeTransitions
//////        if (transitions.isNotEmpty()) {
//////            transitions.forEach { (view, transition) ->
//////                context.transitionRenderers.render(
//////                    context,
//////                    assets,
//////                    program,
//////                    AffineTransform(),
//////                    TransitionView(transition, view)
//////                )
//////            }
//////        } else {
//////            val view = transitionOrchestrator.rootView
//////            if (view != null) {
//////                context.viewRenderers.render(context, assets, program, AffineTransform(), view)
//////            }
//////        }
////
////        // Detach the frame buffer
////        FrameBuffer.unbind(context.gl)
////        context.gl.clear()
////
////        // Set the viewport to the screen size
////        context.gl.setViewport(context.viewport.actualScreenSize)
////        context.gl.setScissor(context.viewport.actualScreenSize)
////        context.gl.enableScissor(true)
////
////        // Apply post-processing
//////        postProcessingProgram.bind()
//////        postProcessingMesh.draw(context.gl)
////        context.gl.enableScissor(false)
////
////        // Swap the buffers so we see the image
////        context.window.swapBuffers()
////    }
//
////    private fun updatePostProcessingActualScreenSize() {
////        postProcessingProgram.drawAreaSize = context.viewport.actualScreenSize
////        postProcessingProgram.updateUniforms()
////
////        postProcessingMesh.reset()
////        postProcessingMesh.putSprite(
////            postProcessingMaterial, AffineTransform(
////                translation = context.viewport.adjustedScreenSize.toVector2f()
////                    .multiply(0.5f)
////                    .plus(context.viewport.offset),
////                scale = context.viewport.adjustedScreenSize.toVector2f(),
////                reflection = Reflection.VERTICAL
////            )
////        )
////        postProcessingMesh.update()
////    }
//
//    companion object {
//        val postProcessUv = Aabb(
//            Vector2f(),
//            Vector2f(1f, 0f),
//            Vector2f(1f, 1f),
//            Vector2f(0f, 1f)
//        )
//    }
//}