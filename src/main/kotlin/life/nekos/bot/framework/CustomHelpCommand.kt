package life.nekos.bot.framework

import life.nekos.bot.framework.annotations.CommandHelp
import life.nekos.bot.utils.Colors
import life.nekos.bot.utils.Database
import life.nekos.bot.utils.Formats
import me.devoxin.flight.api.CommandFunction
import me.devoxin.flight.api.annotations.Command
import me.devoxin.flight.api.context.MessageContext
import me.devoxin.flight.api.entities.Cog
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.utils.messages.MessageCreateData
import java.time.Instant
import kotlin.reflect.full.findAnnotation

class CustomHelpCommand(private val showParameterTypes: Boolean = true) : Cog {
    override fun name(): String = "Bot"

    @Command(aliases = ["commands", "cmds"], description = "Displays bot help.")
    suspend fun help(ctx: MessageContext, command: String?) {
        if (command == null) {
            return sendHelpMenu(ctx)
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

    private suspend fun sendHelpMenu(ctx: MessageContext) {
        val categories = hashMapOf<String, HashSet<CommandFunction>>()
        val helpEmbed = EmbedBuilder()
        val botPrefix = if (ctx.message.isFromGuild) Database.getPrefix(ctx.guild!!.id) ?: "~" else "~"
        for (command in ctx.commandClient.commands.values) {
            if (command.properties.nsfw && ctx.message.isFromGuild && ctx.textChannel?.isNSFW != true) {
                continue
            }

            val category = command.category.lowercase()

            val list = categories.computeIfAbsent(category) {
                hashSetOf()
            }

            list.add(command)
        }

        for (entry in categories.entries.sortedBy { it.key }) {
            val helpMenu = StringBuilder()
            for (cmd in entry.value.sortedBy { it.name }) {
                val description = cmd.properties.description

                helpMenu.append("`")
                    .append("${botPrefix}${cmd.name.padEnd(25, '​')}:")
                    .append("`")
                    .append(truncate(description, 100))
                    .append("\n")
            }
            helpEmbed.addField(toTitleCase(entry.key) + " Commands", helpMenu.toString(), false)
        }

        helpEmbed.setColor(Colors.getEffectiveColor(ctx))
        helpEmbed.setAuthor("${Formats.MAGIC_EMOTE} Command Help", null, ctx.jda.selfUser.avatarUrl)
        helpEmbed.setFooter(
            "Help requested by ${ctx.author.name}",
            ctx.author.avatarUrl

        )
        helpEmbed.setTimestamp(Instant.now())
        helpEmbed.setDescription(Formats.LING_MSG)
        ctx.send(MessageCreateData.fromEmbeds(helpEmbed.build()))
    }

    private fun sendCommandHelp(ctx: MessageContext, command: CommandFunction) {
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
            .joinToString(" ") { it[0].uppercase() + it.substring(1).lowercase() }
    }

    private fun truncate(s: String, maxLength: Int): String {
        if (s.length > maxLength) {
            return s.substring(0, maxLength - 3) + "..."
        }

        return s
    }
}
