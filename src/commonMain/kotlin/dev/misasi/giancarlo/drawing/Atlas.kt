package dev.misasi.giancarlo.drawing

import dev.misasi.giancarlo.crash
import dev.misasi.giancarlo.opengl.OpenGl
import dev.misasi.giancarlo.opengl.Texture

class Atlas (
    gl: OpenGl,
    bitmap: Bitmap,
    private val materials: Map<String, Material>,
    private val materialSets: Map<String, MaterialSet>
) {
    private val texture: Texture = Texture(gl, bitmap)

    fun getMaterial(name: String) : Material {
        return materials[name]
            ?: crash("Material '$name' not found.")
    }

    fun getMaterialSet(name: String) : MaterialSet {
        return materialSets[name]
            ?: crash("MaterialSet '$name' not found.")
    }

//    companion object {
//        private const val DELIMITER = " "
//
//        private fun createMaterial(texture: Texture, data: List<String>) : Material {
//            val name = data[0]
//            val position = Vector2f(data[1].toFloat(), data[2].toFloat())
//            val size = Vector2f(data[3].toFloat(), data[4].toFloat())
//            val uvPosition = position.divide(texture.size)
//            val uvSize = size.divide(texture.size)
//            return Material(name, texture.textureHandle, Point4.create(uvPosition, uvSize), size)
//        }
//
//        private fun createMaterialSet(materials: Map<String, Material>, data: List<String>) : MaterialSet {
//            val prefix = data[0]
//            val frames = materials.values
//                .filter { it.name.startsWith(prefix) }
//                .toList()
//                .sortedBy { it.name }
//            val frameDurationMillis = data[1].toInt()
//            return MaterialSet(prefix, frames, frameDurationMillis)
//        }
//    }
}
