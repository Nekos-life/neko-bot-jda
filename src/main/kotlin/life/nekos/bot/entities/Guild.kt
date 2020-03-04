package life.nekos.bot.entities

import life.nekos.bot.utils.Database
import life.nekos.bot.utils.Klash

data class Guild(
    val id: String,
    var prefix: String? = null,
    var nekoChannel: String? = null,
    var msgCnt: Long = 0L
) {
    fun update(changes: Guild.() -> Unit) {
        changes(this)
        Database.updateEntire(Database.guilds, id, Klash.deconstruct(this))
    }
}

//
//{
//
//    fun update(changes: Guild.() -> Unit) {
//        changes(this)
//
//        Database.update(Database.guilds, id) {
//            "prefix" eq prefix
//            "nekochannel" eq nekoChannel
//            "msgcnt" eq msgCnt
//        }
//    }
//
//    companion object {
//        // TODO: Consider automating this with annotations and reflections.
//        fun fromDocument(doc: Document): Guild {
//            val id = doc.getString("_id")
//            val prefix = doc.getString("prefix")
//            val nekoChannel = doc.getString("nekochannel")
//            val msgCnt = doc.getLong("exp")
//
//            return Guild(id, prefix, nekoChannel, msgCnt)
//        }
//
//        fun empty(id: String): Guild {
//            return Guild(id, null, null, 0L)
//        }
//    }
//
//}
