package me.nobaboy.nobaaddons.events

import me.nobaboy.nobaaddons.events.internal.Event
import me.nobaboy.nobaaddons.events.internal.EventDispatcher
import me.nobaboy.nobaaddons.utils.CooldownManager
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.MinecraftClient
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * [ClientTickEvents.END_CLIENT_TICK] but with a cooldown attached to it.
 *
 * **Note:** The cooldowns used by this event are relatively inaccurate due to the use of [Duration] instead
 * of whole ticks; if you need precise tick cooldowns, consider using [me.nobaboy.nobaaddons.utils.Scheduler]
 * or handling this yourself instead.
 *
 * ## Example
 *
 * ```kt
 * CooldownTickEvent.EVENT.register {
 *     // ... do something you want to guard with a cooldown ...
 *     it.cooldownManager.startCooldown(30.ticks)
 *     // now this event will be skipped for the next (about) 30 ticks, or 1.5 seconds
 * }
 * ```
 *
 * @see me.nobaboy.nobaaddons.utils.Scheduler
 */
data class CooldownTickEvent(val client: MinecraftClient, val cooldownManager: CooldownManager) : Event() {
	companion object {
		private val DUMMY_MANAGER = CooldownManager()

		init {
			ClientTickEvents.END_CLIENT_TICK.register {
				EVENT.invoke(CooldownTickEvent(it, DUMMY_MANAGER))
			}
		}

		val EVENT = Dispatcher()

		inline val Int.ticks: Duration
			get() = (this / 20.0).seconds
	}

	class Dispatcher internal constructor() : EventDispatcher<CooldownTickEvent>() {
		override fun register(listener: (CooldownTickEvent) -> Unit) {
			val cooldownManager = CooldownManager()
			super.register {
				if(!cooldownManager.isOnCooldown()) {
					listener(it.copy(cooldownManager = cooldownManager))
				}
			}
		}
	}
}