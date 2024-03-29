package life.nekos.bot.apis

import life.nekos.bot.Config
import life.nekos.bot.apis.entities.Color
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Request
import okhttp3.ResponseBody
import org.json.JSONObject
import java.io.InputStream
import java.util.concurrent.CompletableFuture

object AlexFlipnote : Api() {
    private val baseUrl = "https://api.alexflipnote.dev".toHttpUrl()

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
            .url("https://api.alexflipnote.dev/colour/$hex")
            .header("Authorization", Config["alex_token"])
            .build()

        return performRequest(request)
            .thenApply(ResponseBody::string)
            .thenApply(::JSONObject)
            .thenApply(Color::fromObject)
    }

    fun coffee(): CompletableFuture<String> {
        val request = Request.Builder()
            .url("https://coffee.alexflipnote.dev/random.json")
            .header("Authorization", Config["alex_token"])
            .build()

        return performRequest(request)
            .thenApply(ResponseBody::string)
            .thenApply(::JSONObject)
            .thenApply { it.getString("file") }
    }
}
