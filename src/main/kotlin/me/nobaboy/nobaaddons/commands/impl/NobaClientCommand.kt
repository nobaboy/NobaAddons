package me.nobaboy.nobaaddons.commands.impl

import com.mojang.brigadier.exceptions.CommandSyntaxException
import dev.celestialfault.commander.client.ClientCommand
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.utils.ErrorManager
import kotlin.reflect.KFunction

open class NobaClientCommand(function: KFunction<*>, instance: Any?) : ClientCommand(function, instance) {
	override fun onError(error: Throwable) {
		if(error is CommandSyntaxException) throw error
		ErrorManager.logError("Command '$name' threw an unhandled error", error, ignorePreviousErrors = true)
	}

	override fun runAsync(runnable: suspend (CoroutineScope) -> Unit): Job = NobaAddons.coroutineScope.launch(block = runnable)
}