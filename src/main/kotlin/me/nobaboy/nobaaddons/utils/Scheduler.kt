package me.nobaboy.nobaaddons.utils

import me.nobaboy.nobaaddons.NobaAddons
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents

object Scheduler {
	private val tasks = mutableListOf<ScheduledTask>()

	init {
		ClientTickEvents.END_CLIENT_TICK.register { tick() }
	}

	fun schedule(delay: Int, repeat: Boolean = false, task: ScheduledTask.() -> Unit): ScheduledTask {
		require(delay >= 0) { "Delay must be a positive number of ticks" }
		return ScheduledTask(task, delay, repeat).also(tasks::add)
	}

	private fun tick() {
		tasks.filter { it.ticksRemaining-- <= 0 }.forEach { it.run() }
	}

	class ScheduledTask(val task: ScheduledTask.() -> Unit, val ticks: Int, val repeat: Boolean = false): Runnable {
		private var cancelled = false
		internal var ticksRemaining = ticks

		fun cancel() {
			cancelled = true
		}

		override fun run() {
			runCatching { task() }.onFailure { NobaAddons.LOGGER.error("Failed to run scheduled method", it) }
			if(repeat && !cancelled) {
				ticksRemaining = ticks
			} else {
				tasks.remove(this)
			}
		}
	}
}
