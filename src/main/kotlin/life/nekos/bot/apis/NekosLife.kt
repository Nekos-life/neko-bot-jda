package life.nekos.bot.apis

import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.ResponseBody
import org.json.JSONObject
import java.util.concurrent.CompletableFuture

typealias StringResponse = CompletableFuture<String>
typealias ObjectResponse = CompletableFuture<JSONObject>

object NekosLife : Api() {
    private val baseUrl = "https://nekos.life/api/v2".toHttpUrl()

    val meme: StringResponse get() = jsonEndpoint("/img/gecg", "url")
    val avatar: StringResponse get() = jsonEndpoint("/img/avatar", "url")
    val slap: StringResponse get() = jsonEndpoint("/img/slap", "url")
    val poke: StringResponse get() = jsonEndpoint("/img/poke", "url")
    val anal: StringResponse get() = jsonEndpoint("/img/anal", "url")
    val pussy: StringResponse get() = jsonEndpoint("/img/pussy", "url")
    val neko: StringResponse get() = jsonEndpoint("/img/neko", "url")
    val fox: StringResponse get() = jsonEndpoint("/img/fox_girl", "url")
    val kuni: StringResponse get() = jsonEndpoint("/img/kuni", "url")
    val hug: StringResponse get() = jsonEndpoint("/img/hug", "url")
    val cuddle: StringResponse get() = jsonEndpoint("/img/cuddle", "url")
    val pat: StringResponse get() = jsonEndpoint("/img/pat", "url")
    val kiss: StringResponse get() = jsonEndpoint("/img/kiss", "url")

    val cat: StringResponse get() = jsonEndpoint("/cat", "cat")
    val why: StringResponse get() = jsonEndpoint("/why", "why")

    val eightBall: ObjectResponse get() = request { path = "/8ball" }
        .thenApply(ResponseBody::string)
        .thenApply(::JSONObject)

    fun chat(text: String, owo: Boolean) = request {
        path = "/chat"
        queryParams = {
            "text" eq text
            if (owo) { "owo" eq "true" }
        }
    }.thenApply(ResponseBody::string)
        .thenApply(::JSONObject)
        .thenApply { it.getString("response") }

    private fun jsonEndpoint(endpoint: String, jsonKey: String) = request { path = endpoint }
        .thenApply(ResponseBody::string)
        .thenApply(::JSONObject)
        .thenApply { it.getString(jsonKey) }

    private fun request(builder: Endpoint.() -> Unit): CompletableFuture<ResponseBody> {
        val endpoint = Endpoint().apply(builder)
            .apply(baseUrl.newBuilder())
            .build()
            .toRequest()

        return performRequest(endpoint)
    }

}
