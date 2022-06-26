package dev.misasi.giancarlo.math

class Matrix4f private constructor(val data: FloatArray) {
    companion object Factory {
        val zero = Matrix4f(floatArrayOf(
            0f, 0f, 0f, 0f,
            0f, 0f, 0f, 0f,
            0f, 0f, 0f, 0f,
            0f, 0f, 0f, 0f
        ))

        val identity = Matrix4f(floatArrayOf(
            1f, 0f, 0f, 0f,
            0f, 1f, 0f, 0f,
            0f, 0f, 1f, 0f,
            0f, 0f, 0f, 1f
        ))

        fun translation(offset: Vector2f) : Matrix4f {
            val result = identity.data.copyOf()
            result[3] = offset.x
            result[7] = offset.y
            return Matrix4f(result)
        }

        fun translation(offset: Vector3f) : Matrix4f {
            val result = identity.data.copyOf()
            result[3] = offset.x
            result[7] = offset.y
            result[11] = offset.z
            return Matrix4f(result)
        }

        fun scale(scale: Vector2f) : Matrix4f {
            val result = identity.data.copyOf()
            result[0] = scale.x
            result[5] = scale.y
            return Matrix4f(result)
        }

        fun scale(scale: Vector3f) : Matrix4f {
            val result = identity.data.copyOf()
            result[0] = scale.x
            result[5] = scale.y
            result[10] = scale.z
            return Matrix4f(result)
        }

        fun ortho(size: Vector2f) : Matrix4f {
            // Left, Right, Bottom, Top, Near, Far
            // 0, fSize.X, fSize.Y, 0, -1.0, 1.0
            // Flipped for OpenGL
            val result = identity.data.copyOf()
            result[0] = 2f / size.x;      // 2 / (right - left)
            result[3] = -1f;              // - (right + left) / (right - left)
            result[5] = 2f / -size.y;     // 2 / (top - bottom)
            result[7] = 1f;               // - (top + bottom) / (top - bottom)
            result[10] = -1f;             // -2 / (far - near)
            result[11] = 0f;              // - (far + near) / (far - near)
            return Matrix4f(result)
        }

        fun lookAt(cameraPosition: Vector3f, cameraTarget: Vector3f, up: Vector3f) : Matrix4f {
            val f = cameraTarget.minus(cameraPosition).normal
            val s = f.cross(up.normal).normal
            val u = s.cross(f)

            val result = identity.data.copyOf()

            result[0] = s.x;
            result[1] = s.y;
            result[2] = s.z;
            result[3] = -s.dot(cameraPosition);

            result[4] = u.x;
            result[5] = u.y;
            result[6] = u.z;
            result[7] = -u.dot(cameraPosition);

            result[8] = -f.x;
            result[9] = -f.y;
            result[10] = -f.z;
            result[11] = f.dot(cameraPosition);

            return Matrix4f(result);
        }
    }

    val transpose by lazy {
        Matrix4f(floatArrayOf(
            data[0], data[4], data[8], data[12],
            data[1], data[5], data[9], data[13],
            data[2], data[6], data[10], data[14],
            data[3], data[7], data[11], data[15]
        ))
    }

    fun multiply(other: Matrix4f) : Matrix4f {
        return multiply(data, other.data)
    }

    private fun multiply(a: FloatArray, b: FloatArray) : Matrix4f {
        return Matrix4f(floatArrayOf(
            a[0] * b[0] + a[1] * b[4] + a[2] * b[8] + a[3] * b[12],
            a[0] * b[1] + a[1] * b[5] + a[2] * b[9] + a[3] * b[13],
            a[0] * b[2] + a[1] * b[6] + a[2] * b[10] + a[3] * b[14],
            a[0] * b[3] + a[1] * b[7] + a[2] * b[11] + a[3] * b[15],
            a[4] * b[0] + a[5] * b[4] + a[6] * b[8] + a[7] * b[12],
            a[4] * b[1] + a[5] * b[5] + a[6] * b[9] + a[7] * b[13],
            a[4] * b[2] + a[5] * b[6] + a[6] * b[10] + a[7] * b[14],
            a[4] * b[3] + a[5] * b[7] + a[6] * b[11] + a[7] * b[15],
            a[8] * b[0] + a[9] * b[4] + a[10] * b[8] + a[11] * b[12],
            a[8] * b[1] + a[9] * b[5] + a[10] * b[9] + a[11] * b[13],
            a[8] * b[2] + a[9] * b[6] + a[10] * b[10] + a[11] * b[14],
            a[8] * b[3] + a[9] * b[7] + a[10] * b[11] + a[11] * b[15],
            a[12] * b[0] + a[13] * b[4] + a[14] * b[8] + a[15] * b[12],
            a[12] * b[1] + a[13] * b[5] + a[14] * b[9] + a[15] * b[13],
            a[12] * b[2] + a[13] * b[6] + a[14] * b[10] + a[15] * b[14],
            a[12] * b[3] + a[13] * b[7] + a[14] * b[11] + a[15] * b[15]
        ))
    }
}