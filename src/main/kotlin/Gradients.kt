class Gradients {
    var dxdu = 0.0F
    var dydu = 0.0F
    var dxdv = 0.0F
    var dydv = 0.0F
    var dxds = 0.0F
    var dyds = 0.0F
    var dxd1 = 0.0F
    var dyd1 = 0.0F
    val overZ = FloatArray(12)

    fun configure(v0: Vertex, v1: Vertex, v2: Vertex) {
        overZ[3] = 1.0F / v0.position.z
        overZ[0] = v0.normal.x * overZ[3]
        overZ[1] = v0.normal.y * overZ[3]
        overZ[2] = v0.normal.z * overZ[3]
        overZ[7] = 1.0F / v1.position.z
        overZ[4] = v1.normal.x * overZ[7]
        overZ[5] = v1.normal.y * overZ[7]
        overZ[6] = v1.normal.z * overZ[7]
        overZ[11] = 1.0F / v2.position.z
        overZ[8] = v2.normal.x * overZ[11]
        overZ[9] = v2.normal.y * overZ[11]
        overZ[10] = v2.normal.z * overZ[11]
        val v0x2x = v0.position.x - v2.position.x
        val v1x2x = v1.position.x - v2.position.x
        val v0y2y = v0.position.y - v2.position.y
        val v1y2y = v1.position.y - v2.position.y
        val oneOverDx = 1.0F / (v1x2x * v0y2y - v0x2x * v1y2y)
        val tx0 = oneOverDx * v0y2y
        val tx1 = oneOverDx * v1y2y
        val ty0 = oneOverDx * v0x2x
        val ty1 = oneOverDx * v1x2x
        dxdu = (overZ[4] - overZ[8]) * tx0 - (overZ[0] - overZ[8]) * tx1
        dydu = (overZ[8] - overZ[4]) * ty0 - (overZ[8] - overZ[0]) * ty1
        dxdv = (overZ[5] - overZ[9]) * tx0 - (overZ[1] - overZ[9]) * tx1
        dydv = (overZ[9] - overZ[5]) * ty0 - (overZ[9] - overZ[1]) * ty1
        dxds = (overZ[6] - overZ[10]) * tx0 - (overZ[2] - overZ[10]) * tx1
        dyds = (overZ[10] - overZ[6]) * ty0 - (overZ[10] - overZ[2]) * ty1
        dxd1 = (overZ[7] - overZ[11]) * tx0 - (overZ[3] - overZ[11]) * tx1
        dyd1 = (overZ[3] - overZ[11]) * ty1 - (overZ[7] - overZ[11]) * ty0
    }
}
