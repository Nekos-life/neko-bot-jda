package life.nekos.bot.framework.annotations

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ArgDoc(val name: String, val desc: String)
