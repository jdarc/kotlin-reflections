import kotlinx.browser.document
import org.khronos.webgl.Uint32Array
import org.khronos.webgl.get
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLImageElement

class Texture private constructor(private val data: Uint32Array) {

    fun sample(u: Int, v: Int) = data[(v shl 9) + u]

    companion object {
        val BLANK = Texture(Uint32Array(512 * 512))

        fun create(image: HTMLImageElement): Texture {
            val canvas = document.createElement("canvas") as HTMLCanvasElement
            canvas.width = image.width
            canvas.height = image.height
            val context = canvas.getContext("2d") as CanvasRenderingContext2D
            context.drawImage(image, 0.0, 0.0)
            val buffer = context.getImageData(0.0, 0.0, image.width.toDouble(), image.height.toDouble()).data.buffer
            return Texture(Uint32Array(buffer))
        }
    }
}
