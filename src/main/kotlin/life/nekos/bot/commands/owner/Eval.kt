package life.nekos.bot.commands.owner

import life.nekos.bot.Loader
import life.nekos.bot.utils.Colors
import life.nekos.bot.utils.extensions.thenException
import me.devoxin.flight.api.annotations.Command
import me.devoxin.flight.api.annotations.Greedy
import me.devoxin.flight.api.context.Context
import me.devoxin.flight.api.entities.Cog
import org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmLocalScriptEngineFactory

class Eval : Cog {
    private val engine = KotlinJsr223JvmLocalScriptEngineFactory().scriptEngine
    private val evalThread = Thread("fuck")

    init {
        evalThread.priority = Thread.MIN_PRIORITY
    }

    @Command(aliases = ["debug"], description = "Eval, duh.", developerOnly = true)
    fun eval(ctx: Context, @Greedy code: String) {
        val bindings = mapOf(
            "bot" to Loader,
            "ctx" to ctx,
            "jda" to ctx.jda,
            "sm" to ctx.jda.shardManager!!,
            "colors" to Colors
        )

        val bindString =
            bindings.map { "val ${it.key} = bindings[\"${it.key}\"] as ${it.value.javaClass.kotlin.qualifiedName}" }
                .joinToString("\n")
        val bind = engine.createBindings()
        bind.putAll(bindings)

        evalThread.run {
            try {
                val result = engine.eval("$bindString\n$code", bind)
                ctx.respond("```\n$result```").thenException {
                    ctx.respond("Response Error\n```\n$it```")
                }
            } catch (e: Exception) {
                val error = e.localizedMessage.split("\n").first()
                ctx.respond("Engine Error\n```\n$error```").thenException { eng ->
                    ctx.respond("Response Error\n```\n$eng```").thenException { res ->
                        eng.printStackTrace()
                        res.printStackTrace()
                    }
                }
            }
        }
    }
}
