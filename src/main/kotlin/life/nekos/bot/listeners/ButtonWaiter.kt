package life.nekos.bot.listeners

import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.hooks.EventListener
import java.util.Objects
import java.util.UUID
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

typealias TestPredicate = (ButtonInteractionEvent) -> Boolean

class ButtonWaiter : EventListener {
    private val scheduler = Executors.newSingleThreadScheduledExecutor()
    private val capturing = mutableSetOf<Capture>()

    override fun onEvent(event: GenericEvent) {
        if (event !is ButtonInteractionEvent) {
            return
        }

        val captures = capturing.filter { it.test(event) }
        capturing.removeAll(captures.filter { it.singleUse }.toSet())

        for (capture in captures) {
            runCatching { capture.handle?.invoke(event) }
        }
    }

    fun newCapture(configure: Capture.() -> Unit) {
        val new = Capture().apply(configure)
        capturing.add(new)

        if (new.lifespan > 0) {
            scheduler.schedule(
                {
                    if (capturing.remove(new)) {
                        new.timeout?.invoke()
                    }
                },
                new.lifespan,
                TimeUnit.MILLISECONDS
            )
        }
    }

    class Capture private constructor(private val id: String) {
        constructor(): this(UUID.randomUUID().toString())

        var test: TestPredicate = { true }
        var handle: ((ButtonInteractionEvent) -> Unit)? = null
        var timeout: (() -> Unit)? = null
        var singleUse: Boolean = false
        var lifespan: Long = 0

        override fun hashCode() = Objects.hashCode(id)

        override fun equals(other: Any?) = other is Capture && other.id == this.id
    }
}
