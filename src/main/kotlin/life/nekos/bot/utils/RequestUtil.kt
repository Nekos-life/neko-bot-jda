package life.nekos.bot.utils

import kotlinx.coroutines.future.await
import okhttp3.*
import org.slf4j.LoggerFactory
import java.io.IOException
import java.util.concurrent.CompletableFuture

object RequestUtil {
    private val httpClient = OkHttpClient()
    private val log = LoggerFactory.getLogger(RequestUtil::class.java)

    class PendingRequest(private val request: Request) {
        fun submit(): CompletableFuture<Response> {
            val fut = CompletableFuture<Response>()

            httpClient.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    fut.completeExceptionally(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        val message = "Request to ${
                            call.request().url()
                        } yielded code: ${response.code()}, message: ${response.message()}"
                        fut.completeExceptionally(IllegalStateException(message))
                        response.close()
                        return
                    }

                    fut.complete(response)
                }
            })

            return fut
        }

        suspend fun await(): Response {
            return submit().await()
        }

    }

    fun request(builder: Request.Builder.() -> Unit): PendingRequest {
        return request(Request.Builder().apply(builder).build())
    }

    fun request(request: Request): PendingRequest {
        return PendingRequest(request)
    }
}
