package life.nekos.bot

import java.io.FileReader
import java.util.*

object Config {
    private const val CONFIG_FILE_NAME = "config.properties"
    private val props = Properties()

    init {
        props.load(FileReader(CONFIG_FILE_NAME))
    }

    operator fun get(key: String): String {
        return props.getProperty(key)
            ?: throw IllegalStateException("Config value for $key is not present!")
    }

}