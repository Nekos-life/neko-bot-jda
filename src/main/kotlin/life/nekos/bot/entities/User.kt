package life.nekos.bot.entities

import life.nekos.bot.utils.Database
import org.bson.Document
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*

data class User(val id: String, var nekos: Long, var nekosAll: Long,
                var exp: Long, var level: Long, var premium: Int,
                var registerDate: String, var optedIn: Boolean, var is18: Boolean, var coolDownCount: Int ) {

    fun update(changes: User.() -> Unit) {
        changes(this)

        Database.update(Database.users, id) {
            "nekos" eq nekos
            "nekosall" eq nekosAll
            "exp" eq exp
            "level" eq level
            "premium" eq premium
            "regdate" eq registerDate
            "opted_in" eq optedIn
            "is_18" eq is18
            "coolDownCount" eq coolDownCount
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
            val coolDownCount = doc.getInteger("coolDownCount")

            return User(id, nekos, nekosAll, exp, level, premium, dateRegistered, optedIn, is18, coolDownCount)
        }

        fun empty(id: String): User {
            val regDate = dateFormatter.format(Date.from(Instant.now()))
            return User(id, 0L, 0L, 0L, 0L, 0, regDate, optedIn = false, is18 = false, coolDownCount = 0)
        }

    }

}
