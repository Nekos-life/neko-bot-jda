package life.nekos.bot.entities

import life.nekos.bot.utils.Database
import org.bson.Document

data class Guild(val id: String, var prefix: String?, var nekoChannel: String?,
                 var msgCnt: Long) {

    fun update(changes: Guild.() -> Unit) {
        changes(this)

        Database.update(Database.users, id) {
            "prefix" eq prefix
            "nekochannel" eq nekoChannel
            "msgcnt" eq msgCnt
        }
    }

    companion object {
        // TODO: Consider automating this with annotations and reflections.
        fun fromDocument(doc: Document): Guild {
            val id = doc.getString("_id")
            val prefix = doc.getString("prefix")
            val nekoChannel = doc.getString("nekochannel")
            val msgCnt = doc.getLong("exp")

            return Guild(id, prefix, nekoChannel, msgCnt)
        }

        fun emptyGuild(id: String): Guild {
            return Guild(id, null, null, 0L)
        }
    }

}
