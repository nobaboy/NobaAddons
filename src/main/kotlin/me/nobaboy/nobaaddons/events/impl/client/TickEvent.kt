package me.nobaboy.nobaaddons.events.impl.client

import me.nobaboy.nobaaddons.events.AbstractEvent
import me.nobaboy.nobaaddons.events.EventDispatcher
import me.nobaboy.nobaaddons.utils.CooldownManager
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.MinecraftClient
import kotlin.reflect.KFunction
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.instanceParameter
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.full.valueParameters

/**
 * Wrapper event around the Fabric API [ClientTickEvents.END_CLIENT_TICK] event
 */
data class TickEvent(val client: MinecraftClient) : AbstractEvent() {
	companion object : EventDispatcher<TickEvent>() {
		init {
			ClientTickEvents.END_CLIENT_TICK.register { invoke(TickEvent(it)) }
		}

		/**
		 * Register the provided [listener] in a wrapper that only invokes it every [nthTick]
		 */
		inline fun every(nthTick: UInt, crossinline listener: (TickEvent) -> Unit) {
			var ticks = 0u
			register {
				if(ticks++ % nthTick != 0u) return@register
				listener(it)
			}
		}

		/**
		 * Convenience alias for `.every(20) { ... }`
		 *
		 * @see every
		 */
		inline fun everySecond(crossinline listener: (TickEvent) -> Unit) {
			every(20u, listener)
		}

		/**
		 * Register the provided [listener] with a cooldown manager
		 *
		 * **Note:** The cooldowns used by this event are relatively inaccurate due to the use of [kotlin.time.Duration] instead
		 * of whole ticks; if you need precise tick cooldowns, consider using [me.nobaboy.nobaaddons.utils.Scheduler]
		 * or handling this yourself instead.
		 *
		 * ## Example
		 *
		 * ```kt
		 * TickEvent.cooldown { event, cooldown ->
		 *     // ... do something you want to guard with a cooldown ...
		 *     cooldown.startCooldown(1.5.seconds)
		 *     // now this event will be skipped for the next (about) 30 ticks, or 1.5 seconds
		 * }
		 * ```
		 */
		inline fun cooldown(crossinline listener: (TickEvent, CooldownManager) -> Unit) {
			val manager = CooldownManager()
			register {
				if(manager.isOnCooldown()) return@register
				listener(it, manager)
			}
		}
	}
}