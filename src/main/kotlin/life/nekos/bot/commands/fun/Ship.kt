package life.nekos.bot.commands.`fun`

import kotlinx.coroutines.future.await
import life.nekos.bot.utils.Formats
import life.nekos.bot.utils.RequestUtil
import me.devoxin.flight.api.annotations.Command
import me.devoxin.flight.api.context.MessageContext
import me.devoxin.flight.api.entities.Cog
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.utils.FileUpload
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder
import java.awt.Color
import java.awt.Font
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.util.*
import javax.imageio.ImageIO

class Ship : Cog {
    private suspend fun loadAvatar(user: User): BufferedImage {
        return RequestUtil.request { url(user.effectiveAvatarUrl) }.submit()
            .thenApply {
                if (!it.isSuccessful) {
                    throw IllegalStateException("Unable to load avatar")
                }

                val bs = it.body?.byteStream()
                    ?: throw IllegalStateException("No image data")

                ImageIO.read(bs)
            }
            .await()
    }

    private fun mixString(a: String, b: String): String = buildString {
        for (i in 0 until a.length.coerceAtLeast(b.length)) {
            if (i <= a.length - 1) append(a, i, i + 1)
            if (i <= b.length - 1) append(b, i, i + 1)
        }
    }

    @Command(aliases = ["love"], description = "Ships user/users")
    suspend fun ship(ctx: MessageContext, user1: User, user2: User = ctx.author) {
        if (user1.idLong == ctx.jda.selfUser.idLong) {
            return ctx.send("Nu nya, your tail's not big enough for me~ >.<")
        }

        if (user1.idLong == ctx.author.idLong && user2.idLong == ctx.author.idLong) {
            return ctx.send("Nu nya, you can't ship yourself~")
        }

        if (user1.idLong == user2.idLong) {
            return ctx.send("Nu nya, you must ship different people~")
        }

        ctx.typingAsync {
            val av1 = loadAvatar(user1)
            val av2 = loadAvatar(user2)
            val randResult = r.nextInt(99 - 10) + 10

            val template = ImageIO.read(this.javaClass.getResource("/catLove.png"))
            val image = template.createGraphics().apply {
                color = Color(51, 232, 211)
                font = Font("Whitney", Font.BOLD, 36)
            }
            image.drawImage(av2, 0, 0, 160, 160, null)
            image.drawImage(av1, 320, 0, 160, 160, null)
            image.drawImage(template, 0, 0, null)
            image.drawString(randResult.toString(), 222, 157)
            image.dispose()

            ByteArrayOutputStream().use {
                ImageIO.setUseCache(false)
                ImageIO.write(template, "png", it)

                val createData = MessageCreateBuilder().setContent("${mixString(user1.name, user2.name)} ${Formats.NEKO_C_EMOTE}")
                    .addFiles(FileUpload.fromData(it.toByteArray(), "shipped.png"))
                    .build()

                ctx.send(createData)
            }
        }
    }

    companion object {
        private val r = Random()
    }
}
