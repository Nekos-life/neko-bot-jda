package life.nekos.bot.utils

import okhttp3.*
import java.io.IOException
import java.util.concurrent.CompletableFuture

open class Api {
    private val httpClient = OkHttpClient()

    data class Endpoint(
        var path: String? = null,
        var queryParams: QueryParams.() -> Unit = {}
    ) {
        fun apply(builder: HttpUrl.Builder): HttpUrl.Builder {
            val paramBuilder = QueryParams().apply(queryParams)

            path?.let {
                builder.addPathSegments(it)
            }

            for ((k, v) in paramBuilder.params) {
                builder.setQueryParameter(k, v)
            }

            return builder
        }
    }

    class QueryParams {
        val params = hashMapOf<String, String>()

        infix fun String.eq(value: String) {
            params[this] = value
        }
    }

    fun HttpUrl.toRequest(): Request {
        return Request.Builder()
            .url(this)
            .build()
    }

    fun performRequest(request: Request): CompletableFuture<ResponseBody> {
        val future = CompletableFuture<ResponseBody>()

        httpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                future.completeExceptionally(e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    future.completeExceptionally(
                        IllegalStateException("Request to ${call.request().url().encodedPath()} yielded code ${response.code()}")
                    )
                    return
                }

                val body = response.body()

                if (body == null) {
                    future.completeExceptionally(
                        IllegalStateException("ResponseBody for request ${call.request().url().encodedPath()} is null!")
                    )
                    return
                }

                future.complete(body)
            }
        })

        return future
    }
}
