package life.nekos.bot

import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder
import net.dv8tion.jda.api.sharding.ShardManager

class NekoBot(private val sm: ShardManager) : ShardManager by sm {

    fun home() = sm.getGuildById(333713662739218433L)

    companion object {
        fun new(options: DefaultShardManagerBuilder.() -> Unit): NekoBot {
            val shardManager = DefaultShardManagerBuilder()
                .apply(options)
                .build()

            return NekoBot(shardManager)
        }
    }

}
