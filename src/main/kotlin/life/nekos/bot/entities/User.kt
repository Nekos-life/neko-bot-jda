package life.nekos.bot.entities

import life.nekos.bot.utils.Database
import org.bson.Document

data class User(val id: String, var nekos: Long, var nekosAll: Long,
                var exp: Long, var level: Long, var premium: Int,
                var name: String, var registerDate: String) {

    fun update(changes: User.() -> Unit) {
        changes(this)

        Database.update(Database.users, id) {
            "nekos" to nekos
            "nekosall" to nekosAll
            "exp" to exp
            "level" to level
            "premium" to premium
            "name" to name
        }
    }

    companion object {
        fun fromDocument(doc: Document): User {
            val id = doc.getString("_id")
            val nekos = doc.getLong("nekos")
            val nekosAll = doc.getLong("nekosall")
            val exp = doc.getLong("exp")
            val level = doc.getLong("level")
            val premium = doc.getInteger("premium")
            val name = doc.getString("name")
            val dateRegistered = doc.getString("regdate")

            return User(id, nekos, nekosAll, exp, level, premium, name, dateRegistered)
        }
    }

}
