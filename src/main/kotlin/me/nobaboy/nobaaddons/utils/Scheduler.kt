package me.nobaboy.nobaaddons.utils

import me.nobaboy.nobaaddons.NobaAddons
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents

object Scheduler {
    private val tasks: MutableList<ScheduledTask> = mutableListOf()

    init {
        ClientTickEvents.END_CLIENT_TICK.register { tick() }
    }

    fun schedule(delay: Int, repeat: Boolean = false, task: Runnable) {
        require(delay >= 0) { "Delay must be a positive number of ticks" }
        tasks.add(ScheduledTask(task, delay, repeat))
    }

    fun tick() {
        tasks.asSequence()
            .filter { it.ticksRemaining-- <= 0 }
            .forEach { it.run() }
    }

    private class ScheduledTask(val task: Runnable, val ticks: Int, val repeat: Boolean = false): Runnable {
        var ticksRemaining = ticks

        override fun run() {
            runCatching { task.run() }.onFailure { NobaAddons.LOGGER.error("Failed to run scheduled method", it) }
            if(repeat) {
                ticksRemaining = ticks
            } else {
                tasks.remove(this)
            }
        }
    }
}
