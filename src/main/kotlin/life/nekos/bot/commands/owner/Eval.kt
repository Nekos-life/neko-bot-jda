package life.nekos.bot.commands.owner

import life.nekos.bot.Loader
import life.nekos.bot.utils.Colors
import me.devoxin.flight.annotations.Command
import me.devoxin.flight.api.Context
import me.devoxin.flight.arguments.Greedy
import me.devoxin.flight.models.Cog
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
                ctx.messageChannel.sendMessage("```\n$result```").queue(null) {
                    ctx.messageChannel.sendMessage("Response Error\n```\n$it```").queue()
                }
            } catch (e: Exception) {
                val error = e.localizedMessage.split("\n").first()
                ctx.messageChannel.sendMessage("Engine Error\n```\n$error```").queue(null) {
                    ctx.messageChannel.sendMessage("Response Error\n```\n$it```").queue(null, { println("fuc") })
                }
            }
        }
    }
}
