package life.nekos.bot.apis

import life.nekos.bot.utils.RequestUtil
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
        return RequestUtil.request(request).submit()
            .thenApply {
                return@thenApply it.body()
                    ?: throw IllegalStateException("ResponseBody for request ${it.request().url().encodedPath()} is null!")
            }
    }
}
