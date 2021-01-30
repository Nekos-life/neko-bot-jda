package life.nekos.bot.apis

import life.nekos.bot.apis.entities.Color
import okhttp3.HttpUrl
import okhttp3.Request
import okhttp3.ResponseBody
import org.json.JSONObject
import java.io.InputStream
import java.util.concurrent.CompletableFuture

object AlexFlipnote : Api() {
    private val baseUrl = HttpUrl.get("https://api.alexflipnote.dev")

    fun didYouMean(topText: String, bottomText: String): CompletableFuture<InputStream> {
        val endpoint = Endpoint("didyoumean") {
            "top" eq topText
            "bottom" eq bottomText
        }.apply(baseUrl.newBuilder())
            .build()
            .toRequest()

        return performRequest(endpoint)
            .thenApply(ResponseBody::byteStream)
    }

    fun color(hex: String): CompletableFuture<Color> {
        val request = Request.Builder()
            .url("https://api.alexflipnote.xyz/colour/$hex")
            .build()

        return performRequest(request)
            .thenApply(ResponseBody::string)
            .thenApply(::JSONObject)
            .thenApply { Color.fromObject(it) }
    }

    fun coffee(): CompletableFuture<String> {
        val request = Request.Builder()
            .url("https://coffee.alexflipnote.xyz/random.json")
            .build()

        return performRequest(request)
            .thenApply(ResponseBody::string)
            .thenApply(::JSONObject)
            .thenApply { it.getString("file") }
    }
}
