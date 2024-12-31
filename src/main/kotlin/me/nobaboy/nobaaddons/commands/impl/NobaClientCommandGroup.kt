package me.nobaboy.nobaaddons.commands.impl

import dev.celestialfault.commander.AbstractCommand
import dev.celestialfault.commander.AbstractCommandGroup
import dev.celestialfault.commander.client.ClientCommandGroup
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import kotlin.reflect.KFunction

class NobaClientCommandGroup(instance: Any) : ClientCommandGroup(instance) {
	override fun createChild(function: KFunction<*>): AbstractCommand<FabricClientCommandSource> =
		NobaClientCommand(function, instance)

	override fun createChildGroup(instance: Any): AbstractCommandGroup<FabricClientCommandSource> =
		NobaClientCommandGroup(instance)
}