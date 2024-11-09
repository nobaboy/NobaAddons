package me.nobaboy.nobaaddons.events

import me.nobaboy.nobaaddons.utils.CooldownManager
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.event.EventFactory
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
 * object Event : CooldownTickEvent {
 *     override fun onTick(client: MinecraftClient) {
 *     	   // ... do something you want to guard with a cooldown ...
 *     	   cooldownManager.startCooldown(30.ticks)
 *     	   // now this event will be skipped for the next (about) 30 ticks, or 1.5 seconds
 *     }
 * }
 * ```
 *
 * @see me.nobaboy.nobaaddons.utils.Scheduler
 */
fun interface CooldownTickEvent {
	fun onTick(client: MinecraftClient)

	// this whole mutable map deal feels janky, but there isn't much else we can do here while working within the
	// constraints of the fabric event system requiring a functional interface.
	val cooldownManager: CooldownManager
		get() = COOLDOWN_MANAGERS.getOrPut(this) { CooldownManager() }

	companion object {
		private val COOLDOWN_MANAGERS = mutableMapOf<CooldownTickEvent, CooldownManager>()

		init {
			ClientTickEvents.END_CLIENT_TICK.register { EVENT.invoker().onTick(it) }
		}

		val EVENT = EventFactory.createArrayBacked(CooldownTickEvent::class.java) { listeners ->
			CooldownTickEvent { client ->
				listeners.forEach {
					if(it.cooldownManager.isOnCooldown()) return@forEach
					it.onTick(client)
				}
			}
		}

		inline val Int.ticks: Duration
			get() = (this / 20.0).seconds
	}
}