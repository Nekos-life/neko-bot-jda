package life.nekos.bot.framework.parsers.stringorbool

import me.devoxin.flight.api.Context
import me.devoxin.flight.internal.parsers.BooleanParser
import me.devoxin.flight.internal.parsers.Parser
import me.devoxin.flight.internal.parsers.StringParser
import java.util.*

class StringOrBool : Parser<StringBool> {
    override fun parse(ctx: Context, param: String): Optional<StringBool> {
        val bool = boolParser.parse(ctx, param)

        if (bool.isEmpty) {
            val str = stringParser.parse(ctx, param)

            if (str.isEmpty) {
                return Optional.empty()
            }

            return Optional.of(StringBool(param))
        }

        return Optional.of(StringBool(bool))
    }

    companion object {
        private val stringParser = StringParser()
        private val boolParser = BooleanParser()
    }
}
