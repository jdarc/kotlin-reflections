import kotlin.math.sqrt

data class Vector3(val x: Float, val y: Float, val z: Float) {

    operator fun plus(rhs: Vector3) = Vector3(x + rhs.x, y + rhs.y, z + rhs.z)

    operator fun minus(rhs: Vector3) = Vector3(x - rhs.x, y - rhs.y, z - rhs.z)

    companion object {
        val ZERO = Vector3(0.0F, 0.0F, 0.0F)

        fun dot(lhs: Vector3, rhs: Vector3) = lhs.x * rhs.x + lhs.y * rhs.y + lhs.z * rhs.z

        fun normalize(src: Vector3): Vector3 {
            val s = 1.0F / sqrt(src.x * src.x + src.y * src.y + src.z * src.z)
            return Vector3(src.x * s, src.y * s, src.z * s)
        }
    }
}
