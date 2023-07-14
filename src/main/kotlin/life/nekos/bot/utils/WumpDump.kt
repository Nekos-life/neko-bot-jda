package life.nekos.bot.utils

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.CompletableFuture

object WumpDump {

    fun paste(doc: String): CompletableFuture<String> {
        return RequestUtil.request {
            url("https://feed-the-wump.us/documents")
            post(doc.toRequestBody("text/plain".toMediaType()))
        }.submit()
            .thenApply { JSONObject(it.body!!.string()) }
            .thenApply { "https://feed-the-wump.us/${it.getString("key")}" }
    }

}