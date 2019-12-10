package life.nekos.bot.entities

import life.nekos.bot.utils.Database
import org.bson.Document
import java.text.SimpleDateFormat
import java.time.Instant

data class User(val id: String, var nekos: Long, var nekosAll: Long,
                var exp: Long, var level: Long, var premium: Int,
                var registerDate: String, var optedIn: Boolean, var is18: Boolean) {

    fun update(changes: User.() -> Unit) {
        changes(this)

        Database.update(Database.users, id) {
            "nekos" to nekos
            "nekosall" to nekosAll
            "exp" to exp
            "level" to level
            "premium" to premium
            "regdate" to registerDate
            "opted_in" to optedIn
            "is_18" to is18
        }
    }

    companion object {
        private val dateFormatter = SimpleDateFormat("MMMM d yyyy, h:mm:ss a")

        fun fromDocument(doc: Document): User {
            val id = doc.getString("_id")
            val nekos = doc.getLong("nekos")
            val nekosAll = doc.getLong("nekosall")
            val exp = doc.getLong("exp")
            val level = doc.getLong("level")
            val premium = doc.getInteger("premium")
            val dateRegistered = doc.getString("regdate")
            val optedIn = doc.getBoolean("opted_in")
            val is18 = doc.getBoolean("is_18")

            return User(id, nekos, nekosAll, exp, level, premium, dateRegistered, optedIn, is18)
        }

        fun emptyUser(id: String): User {
            val regDate = dateFormatter.format(Instant.now())
            return User(id, 0L, 0L, 0L, 0L, 0, regDate, optedIn = false, is18 = false)
        }
    }

}
