package me.nobaboy.nobaaddons.events.impl.client

import me.nobaboy.nobaaddons.events.Event
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

data class TickEvent(val client: MinecraftClient) : Event() {
	companion object : EventDispatcher<TickEvent>() {
		init {
			ClientTickEvents.END_CLIENT_TICK.register { invoke(TickEvent(it)) }
		}

		override fun registerFunction(function: KFunction<*>, instance: Any?) {
			when {
				function.hasAnnotation<Cooldown>() -> registerCooldownFunction(function, instance)
				function.hasAnnotation<Every>() -> registerEveryFunction(function, instance)
				else -> super.registerFunction(function, instance)
			}
		}

		private fun registerEveryFunction(function: KFunction<*>, instance: Any?) {
			require(function.valueParameters.size == 1) { "Provided function must accept exactly one parameter" }
			var ticks = 0u
			val every = function.findAnnotation<Every>()!!.nthTick
			val eventParam = function.valueParameters.first()
			register { event ->
				if(ticks++ % every != 0u) return@register
				function.callBy(buildMap {
					function.instanceParameter?.let { put(it, instance) }
					put(eventParam, event)
				})
			}
		}

		private fun registerCooldownFunction(function: KFunction<*>, instance: Any?) {
			require(function.valueParameters.size == 2) { "Provided function must accept exactly two parameters" }
			val cooldown = CooldownManager()
			val eventParam = function.valueParameters.first { it.type.isSubtypeOf(TickEvent::class.starProjectedType) }
			val cooldownParam = function.valueParameters.first { it.type.isSubtypeOf(CooldownManager::class.starProjectedType) }
			register { event ->
				if(cooldown.isOnCooldown()) return@register
				function.callBy(buildMap {
					function.instanceParameter?.let { put(it, instance) }
					put(eventParam, event)
					put(cooldownParam, cooldown)
				})
			}
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
		 */
		inline fun everySecond(crossinline listener: (TickEvent) -> Unit) {
			every(20u, listener)
		}

		/**
		 * Register the provided [listener] with a cooldown manager
		 */
		inline fun cooldown(crossinline listener: (TickEvent, CooldownManager) -> Unit) {
			val manager = CooldownManager()
			register {
				if(manager.isOnCooldown()) return@register
				listener(it, manager)
			}
		}

		/**
		 * Use on an `EventListener` annotated function to mark that it should only be invoked every [nthTick]
		 *
		 * ## Example
		 *
		 * ```kt
		 * @Event.Listener
		 * @TickEvent.Every(2)
		 * fun everySecondTick(event: TickEvent) {
		 *     // ...
		 * }
		 * ```
		 */
		@MustBeDocumented
		@Target(AnnotationTarget.FUNCTION)
		@Retention(AnnotationRetention.RUNTIME)
		annotation class Every(val nthTick: UInt)

		/**
		 * Use on an `EventListener` annotated function to mark that it should also be given a [CooldownManager];
		 * __this breaks the default requirement that functions should only have one parameter!__
		 *
		 * ## Example
		 *
		 * ```kt
		 * @Event.Listener
		 * @TickEvent.Cooldown
		 * fun cooldownTickListener(event: TickEvent, cooldown: CooldownManager) {
		 *     // ...
		 * }
		 * ```
		 */
		@MustBeDocumented
		@Target(AnnotationTarget.FUNCTION)
		@Retention(AnnotationRetention.RUNTIME)
		annotation class Cooldown
	}
}