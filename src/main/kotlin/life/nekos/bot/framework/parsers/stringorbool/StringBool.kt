package life.nekos.bot.framework.parsers.stringorbool

class StringBool(val entity: Any) {
    val isString by lazy { entity is String }
    val isBool by lazy { entity is Boolean }
}
