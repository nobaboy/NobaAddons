package me.nobaboy.nobaaddons.utils

import me.nobaboy.nobaaddons.NobaAddons
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents

/**
 * Scheduling utility for running methods a certain amount of ticks later.
 *
 * @see me.nobaboy.nobaaddons.events.impl.client.TickEvents.cooldown
 */
object Scheduler {
	private val tasks = mutableListOf<ScheduledTask>()

	init {
		ClientTickEvents.END_CLIENT_TICK.register { tick() }
	}

	fun schedule(delay: Int, repeat: Boolean = false, task: ScheduledTask.() -> Unit): ScheduledTask {
		require(delay >= 0) { "Delay must be a positive number of ticks" }
		return ScheduledTask(delay, repeat, task).also(tasks::add)
	}

	fun scheduleAsync(delay: Int, repeat: Boolean = false, task: suspend ScheduledTask.() -> Unit): ScheduledTask {
		require(delay >= 0) { "Delay must be a positive number of ticks" }
		return ScheduledTask(delay, repeat) { NobaAddons.runAsync { task(this@ScheduledTask) } }.also(tasks::add)
	}

	private fun tick() {
		tasks.filter { it.ticksRemaining-- <= 0 }.forEach { it.run() }
	}

	class ScheduledTask internal constructor(
		private val ticks: Int,
		private val repeat: Boolean = false,
		private val task: ScheduledTask.() -> Unit,
	) {
		private var cancelled = false
		internal var ticksRemaining = ticks

		fun cancel() {
			cancelled = true
		}

		fun run() {
			runCatching { task() }.onFailure { ErrorManager.logError("Scheduled method failed", it) }
			if(repeat && !cancelled) {
				ticksRemaining = ticks
			} else {
				tasks.remove(this)
			}
		}
	}
}
