package me.nobaboy.nobaaddons.utils

import com.google.common.util.concurrent.ThreadFactoryBuilder
import it.unimi.dsi.fastutil.ints.AbstractInt2ObjectMap
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import me.nobaboy.nobaaddons.NobaAddons
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class Scheduler private constructor() {
    companion object {
        val INSTANCE = Scheduler()
    }

    private var currentTick = 0
    private val tasks: AbstractInt2ObjectMap<MutableList<ScheduledTask>> = Int2ObjectOpenHashMap()
    private val executor: ExecutorService = Executors.newSingleThreadExecutor(
        ThreadFactoryBuilder().setNameFormat("NobaAddons-Scheduler-%d").build()
    )

    fun schedule(task: Runnable, delay: Int, multithreaded: Boolean) {
        if (delay < 0) {
            NobaAddons.LOGGER.warn("A task was scheduled with a negative delay")
            return
        }

        addTask(ScheduledTask(task, multithreaded), currentTick + delay)
    }
    fun schedule(task: Runnable, delay: Int) {
        schedule(task, delay, false)
    }

    fun scheduleRecurring(task: Runnable, period: Int, multithreaded: Boolean) {
        if (period <= 0) {
            NobaAddons.LOGGER.warn("A recurring task was scheduled with an invalid period")
            return
        }

        addTask(ScheduledTask(task, period, true, multithreaded), currentTick)
    }
    fun scheduleRecurring(task: Runnable, period: Int) {
        scheduleRecurring(task, period, false)
    }

    fun tick() {
        tasks[currentTick]?.let { currentTasks ->
            currentTasks.forEach { task ->
                if (!executeTask(task, task.multithreaded)) {
                    tasks.getOrPut(currentTick + 1) { mutableListOf() }.add(task)
                }
            }
            tasks.remove(currentTick)
        }
        currentTick++
    }

    private fun executeTask(task: Runnable, multithreaded: Boolean): Boolean {
        if (multithreaded) {
            executor.execute(task)
        } else {
            task.run()
        }
        return true
    }

    private fun addTask(task: ScheduledTask, scheduleTick: Int) {
        tasks.getOrPut(scheduleTick) { mutableListOf() }.add(task)
    }

    data class ScheduledTask(
        val task: Runnable,
        val interval: Int = -1,
        val isRecurring: Boolean = false,
        val multithreaded: Boolean
    ): Runnable {
        constructor(task: Runnable, multithreaded: Boolean) : this(task, -1, false, multithreaded)

        override fun run() {
            task.run()
            if (isRecurring) {
                INSTANCE.addTask(this, INSTANCE.currentTick + interval)
            }
        }
    }
}