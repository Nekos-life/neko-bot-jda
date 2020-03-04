package life.nekos.bot.apis

import life.nekos.bot.apis.entities.Pokemon
import life.nekos.bot.utils.Klash
import okhttp3.HttpUrl
import okhttp3.ResponseBody
import java.util.concurrent.CompletableFuture

object PokeApi : Api() {
    private val baseUrl = HttpUrl.get("https://pokeapi.co/api/v2")

    fun getPokemon(id: Int): CompletableFuture<Pokemon> {
        val ep = Endpoint("/pokemon")
            .apply(baseUrl.newBuilder())
            .addPathSegment(id.toString())
            .build()
            .toRequest()

        return performRequest(ep)
            .thenApply(ResponseBody::string)
            .thenApply { Klash.construct<Pokemon>(it) }
    }

}
