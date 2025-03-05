package me.nobaboy.nobaaddons.utils

import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.events.impl.client.TickEvent

/**
 * Scheduling utility for running methods a certain amount of ticks later.
 *
 * @see me.nobaboy.nobaaddons.events.impl.client.TickEvent.cooldown
 */
object Scheduler {
	private val tasks = mutableListOf<ScheduledTask>()

	init {
		TickEvent.register { tick() }
	}

	/**
	 * Schedule a new task to be executed in a given amount of ticks
	 *
	 * [delay] must be equal to or greater than `0`, or greater than `0` with [repeat].
	 */
	fun schedule(delay: Int, repeat: Boolean = false, task: (ScheduledTask) -> Unit): ScheduledTask {
		require(delay >= 0) { "Delay must be a positive number of ticks" }
		require(!repeat || delay > 0) { "A repeating task cannot have a delay of 0 ticks" }
		return ScheduledTask(delay, repeat, task).also(tasks::add)
	}

	/**
	 * Wrapper around [schedule] allowing for use of a `suspend fun`
	 */
	fun scheduleAsync(
		delay: Int,
		repeat: Boolean = false,
		task: suspend (ScheduledTask) -> Unit
	): ScheduledTask {
		return schedule(delay, repeat) { NobaAddons.runAsync { task(it) } }
	}

	private fun tick() {
		tasks.filter { it.ticksRemaining-- <= 0 }.forEach { it.run() }
	}

	/**
	 * Internal representation of a scheduled task, allowing for canceling repeating tasks
	 */
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

		internal fun run() {
			runCatching { task() }.onFailure { ErrorManager.logError("Scheduled method failed", it) }
			if(repeat && !cancelled) {
				ticksRemaining = ticks
			} else {
				tasks.remove(this)
			}
		}
	}
}