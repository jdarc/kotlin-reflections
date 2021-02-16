import kotlin.math.ceil

class Edge {
    var x = 0.0F
    var y = 0
    var height = 0
    val overZ = FloatArray(4)

    private var xStep = 0.0F
    private val overZStep = FloatArray(4)

    fun configure(v0: Vertex, v1: Vertex, gradients: Gradients, offset: Int) {
        y = ceil(v0.position.y).toInt()
        height = ceil(v1.position.y).toInt() - y

        if (height > 0) {
            val yPreStep = y - v0.position.y
            xStep = (v1.position.x - v0.position.x) / (v1.position.y - v0.position.y)
            x = yPreStep * xStep + v0.position.x

            overZStep[0] = xStep * gradients.dxdu + gradients.dydu
            overZStep[1] = xStep * gradients.dxdv + gradients.dydv
            overZStep[2] = xStep * gradients.dxds + gradients.dyds
            overZStep[3] = xStep * gradients.dxd1 + gradients.dyd1

            val xPreStep = x - v0.position.x
            overZ[0] = yPreStep * gradients.dydu + xPreStep * gradients.dxdu + gradients.overZ[offset + 0]
            overZ[1] = yPreStep * gradients.dydv + xPreStep * gradients.dxdv + gradients.overZ[offset + 1]
            overZ[2] = yPreStep * gradients.dyds + xPreStep * gradients.dxds + gradients.overZ[offset + 2]
            overZ[3] = yPreStep * gradients.dyd1 + xPreStep * gradients.dxd1 + gradients.overZ[offset + 3]
        }
    }

    fun step() {
        ++y
        x += xStep
        overZ[0] += overZStep[0]
        overZ[1] += overZStep[1]
        overZ[2] += overZStep[2]
        overZ[3] += overZStep[3]
    }
}
