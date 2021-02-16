import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.html.*
import kotlinx.html.dom.create
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.Image
import org.w3c.dom.TouchEvent
import org.w3c.dom.events.MouseEvent
import org.w3c.dom.get
import kotlin.math.max
import kotlin.math.min

fun main() {
    window.onload = {
        document.body?.appendChild(document.create.canvas("viewport") { width = "800"; height = "800" })

        document.body?.appendChild(document.create.div("options") {
            arrayOf("Teapot", "Nut & Bolt", "Bart", "Ram", "Beethoven", "King Tut", "Buddha").map {
                val filename = it.toLowerCase().replace(Regex("\\s+|&"), "")
                a("#&mesh=${filename}.obj") { img(it, "images/${filename}.png") { title = it } }
            }
        })

        val canvas = document.querySelector(".viewport") as HTMLCanvasElement
        val graphics = Surface(canvas)
        val light = Vector3.normalize(Vector3(5.0F, 30.0F, -30.0F))
        val dst0 = Vertex()
        val dst1 = Vertex()
        val dst2 = Vertex()
        var mesh: Mesh? = null
        var dragging = false
        var angleX = 0.0F
        var angleY = 0.0F
        var oldX = 0
        var oldY = 0

        fun extractUrlParameters() = if (window.location.href.contains('&') && window.location.href.contains('=')) {
            mapOf(window.location.href.split("&").last().split('=').windowed(2) { Pair(it.first(), it.last()) }.first())
        } else {
            mapOf(Pair("mesh", "teapot.obj"))
        }

        fun loadMesh() {
            window.fetch("./data/${extractUrlParameters()["mesh"]}").then { it.text() }.then { mesh = Mesh.load(it) }
        }

        fun resizeCanvas() {
            val scaleX = (document.body?.offsetWidth ?: 1) / canvas.width.toFloat()
            val scaleY = (document.body?.offsetHeight ?: 1) / canvas.height.toFloat()
            val sca = min(scaleX, scaleY)
            canvas.style.transform = "translateY(-50%) translateX(-50%) scale(${sca}, ${sca})"
        }

        fun transform(src: Vertex, dst: Vertex, world: Matrix3, normal: Matrix3): Vertex {
            dst.position = Matrix3.transform(src.position, world)
            dst.normal = Matrix3.transform(src.normal, normal)
            val dz = dst.position.z + 2.0F
            dst.position = Vector3(graphics.centerX + dst.position.x / dz, graphics.centerY - dst.position.y / dz, dz)
            dst.normal = Vector3(dst.normal.x, dst.normal.y, Vector3.dot(dst.normal, light).coerceIn(0.0F, 1.0F))
            return dst
        }

        window.addEventListener("mousedown", {
            it as MouseEvent
            if (it.button.toInt() == 0) {
                it.preventDefault()
                oldX = it.clientX
                oldY = it.clientY
                dragging = true
            }
        })

        window.addEventListener("mousemove", {
            it as MouseEvent
            it.preventDefault()
            if (dragging) {
                angleY -= (it.clientX - oldX) / 100.0F
                angleX -= (it.clientY - oldY) / 100.0F
                angleX = min(1.57F, max(-1.57F, angleX))
                oldX = it.clientX
                oldY = it.clientY
            }
        })

        window.addEventListener("mouseup", {
            it as MouseEvent
            it.preventDefault()
            dragging = false
        })

        window.addEventListener("touchstart", { e ->
            e as TouchEvent
            if (e.changedTouches.length > 0) {
                oldX = e.changedTouches[0]?.pageX ?: 0
                oldY = e.changedTouches[0]?.pageY ?: 0
            }
            dragging = true
        })

        window.addEventListener("touchmove", { e ->
            e as TouchEvent
            if (dragging && e.changedTouches.length > 0) {
                angleY -= ((e.changedTouches[0]?.pageX ?: 0) - oldX) / 100.0F
                angleX -= ((e.changedTouches[0]?.pageY ?: 0) - oldY) / 100.0F
                angleX = min(1.57F, max(-1.57F, angleX))
                oldX = e.changedTouches[0]?.pageX ?: 0
                oldY = e.changedTouches[0]?.pageY ?: 0
            }
        })

        window.addEventListener("touchend", { dragging = false })

        window.addEventListener("hashchange", { loadMesh() })

        window.addEventListener("resize", { resizeCanvas() })

        fun loop() {
            window.requestAnimationFrame { loop() }
            graphics.clear()
            if (mesh != null) {
                val m = mesh!!
                val vertices = m.vertexBuffer
                if (!dragging) angleY += 0.03F
                val scale = canvas.offsetWidth.toFloat()
                val normal = Matrix3.rotationY(angleY) * Matrix3.rotationX(angleX)
                val world = normal * Matrix3(scale, 0.0F, 0.0F, 0.0F, scale, 0.0F, 0.0F, 0.0F, 1.0F)
                for (i in vertices.indices step 3) {
                    graphics.render(transform(vertices[i + 0], dst0, world, normal),
                                    transform(vertices[i + 1], dst1, world, normal),
                                    transform(vertices[i + 2], dst2, world, normal))
                }
            }
            graphics.update()
        }

        val img = Image()
        img.addEventListener("load", { graphics.texture = Texture.create(img) })
        img.src = "images/environment.jpg"
        resizeCanvas()
        loadMesh()
        loop()
    }
}

