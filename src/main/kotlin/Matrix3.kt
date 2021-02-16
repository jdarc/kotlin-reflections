import kotlin.math.cos
import kotlin.math.sin

data class Matrix3(
    val e00: Float, val e01: Float, val e02: Float,
    val e10: Float, val e11: Float, val e12: Float,
    val e20: Float, val e21: Float, val e22: Float
) {
    operator fun times(m: Matrix3) = Matrix3(
        e00 * m.e00 + e01 * m.e10 + e02 * m.e20,
        e00 * m.e01 + e01 * m.e11 + e02 * m.e21,
        e00 * m.e02 + e01 * m.e12 + e02 * m.e22,
        e10 * m.e00 + e11 * m.e10 + e12 * m.e20,
        e10 * m.e01 + e11 * m.e11 + e12 * m.e21,
        e10 * m.e02 + e11 * m.e12 + e12 * m.e22,
        e20 * m.e00 + e21 * m.e10 + e22 * m.e20,
        e20 * m.e01 + e21 * m.e11 + e22 * m.e21,
        e20 * m.e02 + e21 * m.e12 + e22 * m.e22,
    )

    companion object {

        fun transform(v: Vector3, m: Matrix3) = Vector3(
            (v.x * m.e00) + (v.y * m.e10) + (v.z * m.e20),
            (v.x * m.e01) + (v.y * m.e11) + (v.z * m.e21),
            (v.x * m.e02) + (v.y * m.e12) + (v.z * m.e22)
        )

        fun rotationX(a: Float) = Matrix3(1.0F, 0.0F, 0.0F, 0.0F, cos(a), sin(a), 0.0F, -sin(a), cos(a))

        fun rotationY(a: Float) = Matrix3(cos(a), 0.0F, -sin(a), 0.0F, 1.0F, 0.0F, sin(a), 0.0F, cos(a))
    }
}
