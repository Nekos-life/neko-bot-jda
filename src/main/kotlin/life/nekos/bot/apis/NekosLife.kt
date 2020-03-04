package life.nekos.bot.apis

import okhttp3.HttpUrl
import okhttp3.ResponseBody
import org.json.JSONObject
import java.util.concurrent.CompletableFuture

object NekosLife : Api() {
    private val baseUrl = HttpUrl.get("https://nekos.life/api/v2")

    fun meme() = jsonEndpoint("/img/gecg", "url")
    fun avatar() = jsonEndpoint("/img/avatar", "url")
    fun slap() = jsonEndpoint("/img/slap", "url")
    fun nsfwAvatar() = jsonEndpoint("/img/nsfw_avatar", "url")
    fun poke() = jsonEndpoint("/img/poke", "url")
    fun anal() = jsonEndpoint("/img/anal", "url")
    fun pussy() = jsonEndpoint("/img/pussy", "url")
    fun neko() = jsonEndpoint("/img/neko", "url")
    fun lewd() = jsonEndpoint("/img/lewd", "url")
    fun fox() = jsonEndpoint("/img/fox_girl", "url")
    fun kuni() = jsonEndpoint("/img/kuni", "url")
    fun hug() = jsonEndpoint("/img/hug", "url")
    fun cuddle() = jsonEndpoint("/img/cuddle", "url")
    fun pat() = jsonEndpoint("/img/pat", "url")
    fun kiss() = jsonEndpoint("/img/kiss", "url")

    fun cat() = jsonEndpoint("/cat", "cat")
    fun why() = jsonEndpoint("/why", "why")

    fun eightBall() = request { path = "/8ball" }
        .thenApply(ResponseBody::string)
        .thenApply(::JSONObject)

    fun chat(text: String, owo: Boolean) = request {
        path = "/chat"
        queryParams = {
            "text" eq text

            if (owo) {
                "owo" eq "true"
            }
        }
    }.thenApply(ResponseBody::string)
        .thenApply(::JSONObject)
        .thenApply { it.getString("response") }

    fun jsonEndpoint(endpoint: String, jsonKey: String) = request { path = endpoint }
        .thenApply(ResponseBody::string)
        .thenApply(::JSONObject)
        .thenApply { it.getString(jsonKey) }

    fun request(builder: Endpoint.() -> Unit): CompletableFuture<ResponseBody> {
        val endpoint = Endpoint().apply(builder)
            .apply(baseUrl.newBuilder())
            .build()
            .toRequest()

        return performRequest(endpoint)
    }

}
