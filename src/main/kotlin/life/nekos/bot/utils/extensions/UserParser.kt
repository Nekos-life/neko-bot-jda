package life.nekos.bot.utils.extensions

import me.devoxin.flight.api.Context
import me.devoxin.flight.internal.parsers.Parser
import me.devoxin.flight.internal.parsers.SnowflakeParser
import net.dv8tion.jda.api.entities.User
import java.util.*

class UserParser : Parser<User> {
    override fun parse(ctx: Context, param: String): Optional<User> {
        val snowflake = snowflakeParser.parse(ctx, param)
        val user =
            if (snowflake.isPresent) {
                ctx.message.mentionedUsers.firstOrNull { it.idLong == snowflake.get().resolved }
                    ?: ctx.jda.retrieveUserById(
                        snowflake.get().resolved
                    ).submit().get()
            } else {
                if (param.length > 5 && param[param.length - 5].toString() == "#") {
                    val tag = param.split("#")
                    ctx.guild!!.findMembers { it.user.name == tag[0] && it.user.discriminator == tag[1] }.get()
                        .first().user
                } else {
                    ctx.guild!!.findMembers { it.user.name == param }.get().first().user
                }
            }

        return Optional.ofNullable(user)
    }

    companion object {
        val snowflakeParser = SnowflakeParser() // We can reuse this
    }

}
