package me.nobaboy.nobaaddons.commands.impl

import com.mojang.brigadier.exceptions.CommandSyntaxException
import dev.celestialfault.commander.client.ClientCommand
import me.nobaboy.nobaaddons.utils.ErrorManager
import kotlin.reflect.KFunction

class NobaClientCommand(function: KFunction<*>, instance: Any?) : ClientCommand(function, instance) {
	override fun onError(error: Throwable) {
		if(error is CommandSyntaxException) throw error
		ErrorManager.logError("Command '$name' threw an unhandled error", error, ignorePreviousErrors = true)
	}
}