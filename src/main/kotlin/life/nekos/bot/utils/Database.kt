package life.nekos.bot.utils


import com.mongodb.BasicDBObject
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.UpdateOptions
import life.nekos.bot.entities.Guild
import life.nekos.bot.entities.User
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.bson.Document
import java.util.*
import java.util.concurrent.ThreadLocalRandom


object Database {
    val mango = MongoClients.create()

    val neko = mango.getDatabase("neko")
    val users = neko.getCollection("users")
    val guilds = neko.getCollection("guilds")

    fun getPrefix(guildId: String) = getFrom(guilds, guildId) { getString("prefix") }

    fun getGuild(guildId: String) = getFrom(guilds, guildId) { Guild.fromDocument(this) }
        ?: Guild.emptyGuild(guildId)

    fun getUser(userId: String) = getFrom(users, userId) { User.fromDocument(this) }
        ?: User.emptyUser(userId)

    fun getDocument(c: MongoCollection<Document>, id: String) = c.find(BasicDBObject("_id", id)).firstOrNull()

    fun <T> getFrom(c: MongoCollection<Document>, id: String, apply: Document.() -> T): T? {
        return getDocument(c, id)?.apply()
    }

    fun update(c: MongoCollection<Document>, id: String, apply: Mapper.() -> Unit) {
        val updated = Mapper().apply(apply).doc

        c.updateOne(
            eq("_id", id),
            Document("\$set", updated),
            UpdateOptions().upsert(true)
        )
    }

    class Mapper {
        val doc = Document()

        infix fun String.eq(other: Any) {
            doc.append(this, other)
        }
    }
}

