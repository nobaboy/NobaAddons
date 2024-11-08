package me.nobaboy.nobaaddons.events.skyblock

import me.nobaboy.nobaaddons.utils.Timestamp
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.client.MinecraftClient
import kotlin.time.Duration.Companion.seconds

fun interface SecondPassedEvent {
	fun onSecond(client: MinecraftClient)

	companion object {
		private var lastSecond = Timestamp.distantPast()

		init {
			ClientTickEvents.END_CLIENT_TICK.register { client ->
				if(lastSecond.elapsedSince() >= 1.seconds) {
					EVENT.invoker().onSecond(client)
					lastSecond = Timestamp.now()
				}
			}
		}

		val EVENT = EventFactory.createArrayBacked(SecondPassedEvent::class.java) { listeners ->
			SecondPassedEvent { client ->
				listeners.forEach { it.onSecond(client) }
			}
		}
	}
}