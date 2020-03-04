package life.nekos.bot.apis

import okhttp3.HttpUrl
import java.util.concurrent.CompletableFuture

object PokeApi : Api() {
    private val baseUrl = HttpUrl.get("https://pokeapi.co/api/v2")

    fun getPokemon(id: Int): CompletableFuture<String> {
        val ep = Endpoint("/pokemon")
            .apply(baseUrl.newBuilder())
            .addPathSegment(id.toString())
            .build()
            .toRequest()

        return performRequest(ep)
            .thenApply { "" /* TODO */ }
    }

}
