package life.nekos.bot.utils

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import life.nekos.bot.Loader.bot
import life.nekos.bot.NekoBot
import net.dv8tion.jda.api.JDA
import org.json.JSONArray
import org.json.JSONObject


class Server {

    fun server(port: Int?) {
        embeddedServer(
            Netty,
            port = port ?: 8080,
            module = Application::api
        ).apply { start(wait = false) }

    }

}


private fun getPings(): JSONArray {
    return JSONArray().also {
        for ((jda, status) in bot.statuses) {
            val obj = JSONObject()
                .put("shard", jda.shardInfo.shardId)
                .put("ping", jda.gatewayPing)
                .put("status", status)

            it.put(obj)
        }
    }
}


fun Application.api() {
    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.Authorization)
        anyHost()
    }

    routing {
        get("/") {
            call.respondText("test", contentType = ContentType.Text.Plain)
        }

        get("/health") {
            val health =
                if (bot.shards.all { it.status == JDA.Status.CONNECTED || it.status == JDA.Status.LOADING_SUBSYSTEMS }) "ok" else "warn"
            call.respondText(
                "{\"health\": \"$health\", \"ping\": ${bot.averageGatewayPing.toInt()}}",
                ContentType.Application.Json
            )
        }

        get("/pings") {
            call.respondText("{\"status\": ${getPings()}}", ContentType.Application.Json)
        }

        get("/metrics") {
            call.respondText(
                "{\"metrics\": ${NekoBot.metrics.render().get()}}",
                ContentType.Application.Json
            )
        }
    }

}
