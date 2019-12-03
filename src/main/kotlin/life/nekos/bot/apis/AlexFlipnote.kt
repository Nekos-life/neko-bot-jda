package life.nekos.bot.apis

import okhttp3.*
import org.json.JSONObject
import java.io.InputStream
import java.util.concurrent.CompletableFuture

object AlexFlipnote : Api() {
    private val baseUrl = HttpUrl.get("https://api.alexflipnote.xyz")

    fun didYouMean(topText: String, bottomText: String): CompletableFuture<InputStream> {
        val endpoint = Endpoint("didyoumean") {
            "top" eq topText
            "bottom" eq bottomText
        }.apply(baseUrl.newBuilder())
            .build()
            .toRequest()

        return performRequest(endpoint)
            .thenApply { it.byteStream() }
    }

    fun color(hex: String): CompletableFuture<Color> {
        val request = Request.Builder()
            .url("https://api.alexflipnote.xyz/colour/$hex")
            .build()

        return performRequest(request)
            .thenApply { it.string() }
            .thenApply { createColorFromJsonString(it) }
    }

    fun coffee(): CompletableFuture<String> {
        val request = Request.Builder()
            .url("https://coffee.alexflipnote.xyz/random.json")
            .build()

        return performRequest(request)
            .thenApply { it.string() }
            .thenApply { JSONObject(it) }
            .thenApply { it.getString("file") }
    }

    class Color(
        val textContrast: String,
        val brightness: Int,
        val hex: String,
        val imageUrl: String,
        val gradientImageUrl: String,
        val integer: Int,
        val name: String,
        val rgb: String,
        val rgbValues: RGB,
        val shade: List<String>,
        val tint: List<String>
    )

    class RGB(val r: Int, val g: Int, val b: Int)

    fun createColorFromJsonString(str: String): Color {
        val json = JSONObject(str)
        val textContrast = json.getString("blackorwhite_text")
        val brightness = json.getInt("brightness")
        val hex = json.getString("hex")
        val imageUrl = json.getString("image")
        val gradientImageUrl = json.getString("image_gradient")
        val integer = json.getInt("int")
        val name = json.getString("name")
        val rgb = json.getString("rgb")

        val rgbValuesJson = json.getJSONObject("rgb_values")
        val rgbValues = RGB(
            rgbValuesJson.getInt("r"),
            rgbValuesJson.getInt("g"),
            rgbValuesJson.getInt("b")
        )

        val shade = json.getJSONArray("shade").map { it as String }.toList()
        val tint = json.getJSONArray("tint").map { it as String }.toList()

        return Color(
            textContrast,
            brightness,
            hex,
            imageUrl,
            gradientImageUrl,
            integer,
            name,
            rgb,
            rgbValues,
            shade,
            tint
        )
    }
}
