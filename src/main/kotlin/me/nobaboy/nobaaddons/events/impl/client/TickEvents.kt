package me.nobaboy.nobaaddons.events.impl.client

import me.nobaboy.nobaaddons.events.Event
import me.nobaboy.nobaaddons.events.EventDispatcher
import me.nobaboy.nobaaddons.utils.CooldownManager
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.MinecraftClient
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

object TickEvents {
	init {
		ClientTickEvents.END_CLIENT_TICK.register { TICK.invoke(Tick(it)) }
	}

	/**
	 * Event invoked at the end of every client tick.
	 *
	 * This event is a wrapper around the Fabric API event.
	 */
	val TICK = EventDispatcher<Tick>()

	/**
	 * Utility method to register a [TICK] listener that only runs every [nthTick]
	 */
	inline fun every(nthTick: Int, crossinline event: (Tick) -> Unit) {
		val nthTick = nthTick.also { require(it > 0) { "Provided value must be a positive non-zero integer" } }.toUInt()
		var ticks = 0u
		TICK.register {
			if(ticks++ % nthTick == 0u) {
				event(it)
			}
		}
	}

	/**
	 * Convenience method to add a tick listener for every 20th tick
	 */
	inline fun everySecond(crossinline tick: (Tick) -> Unit) = every(20, tick)

	/**
	 * Utility method to register a [TICK] listener but with a [CooldownManager] attached to it
	 *
	 * **Note:** The cooldowns used by this event are relatively inaccurate due to the use of [kotlin.time.Duration] instead
	 * of whole ticks; if you need precise tick cooldowns, consider using [me.nobaboy.nobaaddons.utils.Scheduler]
	 * or handling this yourself instead.
	 *
	 * ## Example
	 *
	 * ```kt
	 * TickEvents.cooldown { event, cooldown ->
	 *     // ... do something you want to guard with a cooldown ...
	 *     cooldown.startCooldown(1.5.seconds)
	 *     // now this event will be skipped for the next (about) 30 ticks, or 1.5 seconds
	 * }
	 * ```
	 *
	 * @see me.nobaboy.nobaaddons.utils.Scheduler
	 */
	inline fun cooldown(defaultCooldown: Duration = 3.seconds, crossinline event: (Tick, CooldownManager) -> Unit) {
		val manager = CooldownManager(defaultCooldown)
		TICK.register {
			if(manager.isOnCooldown()) return@register
			event(it, manager)
		}
	}

	data class Tick(val client: MinecraftClient) : Event()
}