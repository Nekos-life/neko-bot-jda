package life.nekos.bot.utils


import com.mongodb.BasicDBObject
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.bson.Document
import java.util.*
import java.util.concurrent.ThreadLocalRandom


/**
 * Created by Tom on 10/4/2017.
 */
object Database {
    val mango = MongoClients.create()

    val neko = mango.getDatabase("neko")
    val users = neko.getCollection("users")
    val guilds = neko.getCollection("guilds")

    fun getPrefix(guildId: String) = getFrom(guilds, guildId) { getString("prefix") }



    fun <T> getFrom(c: MongoCollection<Document>, id: String, apply: Document.() -> T): T? {
        val doc = c.find(BasicDBObject("_id", id))
            .firstOrNull() ?: return null

        return apply(doc)
    }
}

