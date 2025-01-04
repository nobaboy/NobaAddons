package me.nobaboy.nobaaddons.events

import me.nobaboy.nobaaddons.events.internal.Event
import me.nobaboy.nobaaddons.events.internal.EventDispatcher
import me.nobaboy.nobaaddons.utils.Timestamp
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.MinecraftClient
import kotlin.time.Duration.Companion.seconds

data class QuarterSecondPassedEvent(val client: MinecraftClient) : Event() {
	companion object {
		private var lastSecond = Timestamp.distantPast()

		init {
			ClientTickEvents.END_CLIENT_TICK.register { client ->
				if(lastSecond.elapsedSince() >= 0.25.seconds) {
					EVENT.invoke(QuarterSecondPassedEvent(client))
					lastSecond = Timestamp.now()
				}
			}
		}

		val EVENT = EventDispatcher<QuarterSecondPassedEvent>()
	}
}