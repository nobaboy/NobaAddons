package me.nobaboy.nobaaddons.events.skyblock

import me.nobaboy.nobaaddons.events.internal.EventDispatcher
import me.nobaboy.nobaaddons.utils.Timestamp
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.MinecraftClient
import kotlin.time.Duration.Companion.seconds

data class SecondPassedEvent(val client: MinecraftClient) {
	companion object {
		private var lastSecond = Timestamp.distantPast()

		init {
			ClientTickEvents.END_CLIENT_TICK.register { client ->
				if(lastSecond.elapsedSince() >= 1.seconds) {
					EVENT.invoke(SecondPassedEvent(client))
					lastSecond = Timestamp.now()
				}
			}
		}

		val EVENT = EventDispatcher<SecondPassedEvent>()
	}
}