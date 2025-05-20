package me.nobaboy.nobaaddons.commands

import com.mojang.brigadier.CommandDispatcher
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap
import me.nobaboy.nobaaddons.commands.impl.Context
import me.nobaboy.nobaaddons.utils.mc.TextUtils.darkGray
import me.nobaboy.nobaaddons.utils.mc.chat.ChatUtils
import me.nobaboy.nobaaddons.utils.tr
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource

object InstanceCommands {
	private val floors = Int2ObjectArrayMap<String>(7).apply {
		put(1, "one")
		put(2, "two")
		put(3, "three")
		put(4, "four")
		put(5, "five")
		put(6, "six")
		put(7, "seven")
	}

	private val kuudraTiers = Int2ObjectArrayMap<String>(5).apply {
		put(1, "normal")
		put(2, "hot")
		put(3, "burning")
		put(4, "fiery")
		put(5, "infernal")
	}

	private val INSTANCE_COMMANDS = buildMap {
		for(floor in 1..7) {
			put("f$floor", "catacombs_floor_${floors[floor]}")
			put("m$floor", "master_catacombs_floor_${floors[floor]}")
		}
		for(tier in 1..5) {
			put("t$tier", "kuudra_${kuudraTiers[tier]}")
		}
	}

	internal fun register(dispatcher: CommandDispatcher<FabricClientCommandSource>) {
		// we unfortunately have to build these commands ourselves, as there's no way to use commander and still
		// be able to get the command name from the provided context; at least, not without doing some ugly
		// hacky workarounds (which would still be more work than just building the commands ourselves)
		INSTANCE_COMMANDS.forEach { command, instance ->
			dispatcher.register(ClientCommandManager.literal(command).executes(joinInstanceCommand(command, instance)))
		}
	}

	fun joinInstance(name: String) {
		joinInstance(name, INSTANCE_COMMANDS[name.lowercase()] ?: return)
	}

	fun joinInstance(name: String, instance: String) {
		ChatUtils.addMessage(tr("nobaaddons.commands.joinInstance", "Joining ${name.uppercase()}").darkGray())
		ChatUtils.queueCommand("joininstance $instance")
	}

	private fun joinInstanceCommand(name: String, instance: String): (Context) -> Int = {
		joinInstance(name, instance)
		0
	}
}