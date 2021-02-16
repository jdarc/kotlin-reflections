import org.khronos.webgl.Uint32Array
import org.khronos.webgl.set
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import kotlin.math.ceil

class Surface(canvas: HTMLCanvasElement) {

    private val context = canvas.getContext("2d") as CanvasRenderingContext2D
    private var imageData = context.createImageData(canvas.width.toDouble(), canvas.height.toDouble())
    private var colorBuffer = Uint32Array(imageData.data.buffer)
    private var depthBuffer = FloatArray(canvas.width * canvas.height)

    private val gradients = Gradients()
    private val edges = arrayOf(Edge(), Edge(), Edge())

    private val width = canvas.width

    val centerX = canvas.width / 2.0F
    val centerY = canvas.height / 2.0F
    var texture = Texture.BLANK

    fun clear() {
        depthBuffer.fill(Float.POSITIVE_INFINITY)
        for (i in 0..colorBuffer.length) colorBuffer[i] = 0
    }

    fun update() = context.putImageData(imageData, 0.0, 0.0)

    fun render(v0: Vertex, v1: Vertex, v2: Vertex) {
        if (isBackFacing(v0, v1, v2)) return
        gradients.configure(v0, v1, v2)

        var leftIndex = if (v0.position.y < v1.position.y) {
            if (v2.position.y < v0.position.y) {
                edges[0].configure(v2, v1, gradients, 8)
                edges[1].configure(v2, v0, gradients, 8)
                edges[2].configure(v0, v1, gradients, 0)
                0
            } else {
                if (v1.position.y < v2.position.y) {
                    edges[0].configure(v0, v2, gradients, 0)
                    edges[1].configure(v0, v1, gradients, 0)
                    edges[2].configure(v1, v2, gradients, 4)
                    0
                } else {
                    edges[0].configure(v0, v1, gradients, 0)
                    edges[1].configure(v0, v2, gradients, 0)
                    edges[2].configure(v2, v1, gradients, 8)
                    1
                }
            }
        } else {
            if (v2.position.y < v1.position.y) {
                edges[0].configure(v2, v0, gradients, 8)
                edges[1].configure(v2, v1, gradients, 8)
                edges[2].configure(v1, v0, gradients, 4)
                1
            } else {
                if (v0.position.y < v2.position.y) {
                    edges[0].configure(v1, v2, gradients, 4)
                    edges[1].configure(v1, v0, gradients, 4)
                    edges[2].configure(v0, v2, gradients, 0)
                    1
                } else {
                    edges[0].configure(v1, v0, gradients, 4)
                    edges[1].configure(v1, v2, gradients, 4)
                    edges[2].configure(v2, v0, gradients, 8)
                    0
                }
            }
        }

        var rightIndex = 1 - leftIndex
        var height = edges[1].height
        var total = edges[0].height
        var y = edges[0].y * width
        while (total > 0) {
            total -= height
            val left = edges[leftIndex]
            val right = edges[rightIndex]
            while (--height >= 0) {
                val xStart = ceil(left.x).toInt()
                var scan = ceil(right.x).toInt() - xStart
                val xPreStep = xStart - left.x
                var uOverZ = left.overZ[0] + xPreStep * gradients.dxdu
                var vOVerZ = left.overZ[1] + xPreStep * gradients.dxdv
                var sOverZ = left.overZ[2] + xPreStep * gradients.dxds
                var oOverZ = left.overZ[3] + xPreStep * gradients.dxd1
                var mem = y + xStart
                while (scan-- > 0) {
                    val z = 1.0F / oOverZ
                    if (z < depthBuffer[mem]) {
                        depthBuffer[mem] = z
                        val u = 256 + (uOverZ * z * 256.0F).toInt()
                        val v = 256 + (vOVerZ * z * 256.0F).toInt()
                        val s = (sOverZ * z * 256.0F).toInt()
                        val env = texture.sample(u, v)
                        val spec = SPECULAR_MAP[s]
                        val red = (spec + (s * (env shr 0x10 and 0xFF) shr 8)).coerceIn(0, 255)
                        val grn = (spec + (s * (env shr 0x08 and 0xFF) shr 8)).coerceIn(0, 255)
                        val blu = (spec + (s * (env shr 0x00 and 0xFF) shr 8)).coerceIn(0, 255)
                        colorBuffer[mem] = 0xFF shl 24 or red.shl(16) or grn.shl(8) or blu
                    }
                    uOverZ += gradients.dxdu
                    vOVerZ += gradients.dxdv
                    sOverZ += gradients.dxds
                    oOverZ += gradients.dxd1
                    ++mem
                }
                left.step()
                right.step()
                y += width
            }
            height = edges[2].height
            leftIndex = leftIndex shl 1
            rightIndex = rightIndex shl 1
        }
    }

    companion object {
        private val SPECULAR_MAP = intArrayOf(
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1,
            1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 4, 4, 4, 4, 5, 5, 5, 6, 6, 6, 7, 7,
            7, 8, 8, 9, 9, 10, 10, 11, 11, 12, 13, 13, 14, 15, 15, 16, 17, 18, 19, 20, 21,
            22, 23, 24, 25, 26, 27, 29, 30, 31, 33, 34, 36, 37, 39, 41, 43, 44, 46, 48,
            51, 53, 55, 57, 60, 62, 65, 68, 70, 73, 76, 80, 83, 86, 90, 93, 97, 101, 105,
            109, 114, 118, 123, 127, 132, 137, 143, 148, 154, 160, 166, 172, 178, 185,
            192, 199, 206, 214, 222, 230, 238, 247, 255
        )

        private fun isBackFacing(a: Vertex, b: Vertex, c: Vertex) =
            (b.position.x - a.position.x) * (c.position.y - a.position.y) -
            (c.position.x - a.position.x) * (b.position.y - a.position.y) < 0.0F
    }
}
