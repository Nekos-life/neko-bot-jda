package life.nekos.bot.framework

import life.nekos.bot.framework.annotations.CommandHelp
import me.devoxin.flight.api.CommandFunction
import me.devoxin.flight.api.annotations.Command
import me.devoxin.flight.api.Context
import me.devoxin.flight.api.entities.Cog
import me.devoxin.flight.internal.utils.TextSplitter
import kotlin.reflect.full.findAnnotation

class CustomHelpCommand(private val showParameterTypes: Boolean = true) : Cog {
    override fun name(): String = "Bot"

    @Command(aliases = ["commands", "cmds"], description = "Displays bot help.")
    suspend fun help(ctx: Context, command: String?) {
        if (command == null) {
            sendHelpMenu(ctx)
            return
        }

        val commands = ctx.commandClient.commands
        val cmd = commands[command]
            ?: commands.values.firstOrNull { it.properties.aliases.contains(command) }

        if (cmd == null) {
            ctx.send("No commands matching `${ctx.cleanContent(command)}` found.")
            return
        }

        sendCommandHelp(ctx, cmd)
    }

    private suspend fun sendHelpMenu(ctx: Context) {
        val categories = hashMapOf<String, HashSet<CommandFunction>>()
        val helpMenu = StringBuilder()

        for (command in ctx.commandClient.commands.values) {
            val category = command.category.toLowerCase()

            val list = categories.computeIfAbsent(category) {
                hashSetOf()
            }

            list.add(command)
        }

        for (entry in categories.entries.sortedBy { it.key }) {
            helpMenu.append(toTitleCase(entry.key)).append("\n")

            for (cmd in entry.value.sortedBy { it.name }) {
                val description = cmd.properties.description

                helpMenu.append("  ")
                    .append(cmd.name.padEnd(15, ' '))
                    .append(" ")
                    .append(truncate(description, 100))
                    .append("\n")
            }
        }

        val pages = TextSplitter.split(helpMenu.toString().trim(), 1990)

        for (page in pages) {
            ctx.sendAsync("```\n$page```")
        }
    }

    private fun sendCommandHelp(ctx: Context, command: CommandFunction) {
        val builder = StringBuilder("```\n")

        if (ctx.trigger.matches("<@!?${ctx.jda.selfUser.id}> ".toRegex())) {
            builder.append("@${ctx.jda.selfUser.name} ")
        } else {
            builder.append(ctx.trigger)
        }

        val properties = command.properties

        if (properties.aliases.isNotEmpty()) {
            builder.append("[")
                .append(command.name)
                .append(properties.aliases.joinToString("|", prefix = "|"))
                .append("] ")
        } else {
            builder.append(command.name)
                .append(" ")
        }

        val args = command.arguments

        for (arg in args) {
            if (!arg.optional) {
                builder.append("<")
            } else {
                builder.append("[")
            }

            builder.append(arg.name)

            if (showParameterTypes) {
                builder.append(": ")
                    .append(arg.type.simpleName)
            }

            if (!arg.optional) {
                builder.append(">")
            } else {
                builder.append("]")
            }
            builder.append(" ")
        }

        builder.trim()

        val description = properties.description
        val extendedDescriptionAnnotation = command.method.findAnnotation<CommandHelp>()
        val extendedDescription = extendedDescriptionAnnotation?.let { "\n\n${it.help.trimIndent()}" }
            ?: ""

        builder.append("\n\n")
            .append(description)
            .append(extendedDescription)
            .append("```")

        ctx.send(builder.toString())
    }

    private fun toTitleCase(s: String): String {
        return s.split(" +".toRegex())
            .joinToString(" ") { it[0].toUpperCase() + it.substring(1).toLowerCase() }
    }

    private fun truncate(s: String, maxLength: Int): String {
        if (s.length > maxLength) {
            return s.substring(0, maxLength - 3) + "..."
        }

        return s
    }
}
