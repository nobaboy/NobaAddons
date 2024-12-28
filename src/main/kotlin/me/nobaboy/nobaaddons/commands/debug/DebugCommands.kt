package me.nobaboy.nobaaddons.commands.debug

import com.mojang.brigadier.context.CommandContext
import me.nobaboy.nobaaddons.api.DebugAPI
import me.nobaboy.nobaaddons.api.PartyAPI
import me.nobaboy.nobaaddons.api.skyblock.MayorAPI
import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI
import me.nobaboy.nobaaddons.commands.internal.Command
import me.nobaboy.nobaaddons.commands.internal.Group
import me.nobaboy.nobaaddons.core.UpdateNotifier
import me.nobaboy.nobaaddons.core.mayor.Mayor
import me.nobaboy.nobaaddons.utils.ErrorManager
import me.nobaboy.nobaaddons.utils.TextUtils.buildText
import me.nobaboy.nobaaddons.utils.TextUtils.toText
import me.nobaboy.nobaaddons.utils.annotations.UntranslatedMessage
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import me.nobaboy.nobaaddons.utils.sound.SoundUtils
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import kotlin.jvm.optionals.getOrNull

@Suppress("unused")
object DebugCommands : Group("debug") {
	internal fun MutableText.data(vararg items: Pair<String, Any?>) {
		items.forEach {
			append(Text.literal("${it.first}: ").formatted(Formatting.BLUE))
			append(it.second as? Text ?: Text.literal(it.second.toString()).formatted(Formatting.GRAY))
			append("\n")
		}
	}

	internal fun CommandContext<FabricClientCommandSource>.dumpInfo(vararg items: Pair<String, Any?>) {
		val text = buildText {
			append("-".repeat(20).toText().formatted(Formatting.DARK_GRAY, Formatting.BOLD))
			append("\n")
			data(*items)
			append("-".repeat(20).toText().formatted(Formatting.DARK_GRAY, Formatting.BOLD))
		}
		source.sendFeedback(text)
	}

	val item = ItemDebugCommands
	val pet = PetDebugCommands
	val regex = RepoDebugCommands

	val party = Command("party") { PartyAPI.listMembers() }

	val mayor = Command("mayor") {
		val mayor = MayorAPI.currentMayor
		val minister = MayorAPI.currentMinister

		if(mayor == Mayor.UNKNOWN && minister == Mayor.UNKNOWN) {
			it.source.sendError(Text.literal("Current Mayor and Minister are still unknown"))
			return@Command
		}

		it.dumpInfo(
			"Current Mayor" to mayor.mayorName,
			"Mayor Perks" to mayor.activePerks,
			"Current Minister" to minister.mayorName,
			"Minister Perk" to minister.activePerks,
		)
	}

	object Sounds : Group("sounds") {
		override val root = RootCommand { if(DebugAPI.isAwtAvailable) DebugAPI.openSoundDebugMenu() }

		val playRareDrop = Command("raredrop") {
			SoundUtils.rareDropSound.play()
		}
	}

	val location = Command("location") {
		val location = DebugAPI.lastLocationPacket
		it.dumpInfo(
			"Server" to location.serverName,
			"Type" to location.serverType.getOrNull(),
			"Lobby" to location.lobbyName.getOrNull(),
			"Mode" to location.mode.getOrNull(),
			"Map" to location.map.getOrNull(),
			"Detected Island" to SkyBlockAPI.currentIsland,
			"Zone" to SkyBlockAPI.currentZone,
		)
	}

	@OptIn(UntranslatedMessage::class)
	val clickaction = Command("clickaction") {
		ChatUtils.addMessageWithClickAction("Click me!") { ChatUtils.addMessage("You clicked me!") }
	}

	val error = Command("error") {
		ErrorManager.logError(
			"Debug error",
			Error("Intentional debug error"),
			"THIS COMMAND INTENTIONALLY THROWS AN ERROR FOR DEBUGGING PURPOSES" to "DO NOT REPORT THIS IN THE DISCORD",
			"Intentionally erroring value" to object {
				override fun toString(): String = throw RuntimeException()
			},
			ignorePreviousErrors = true
		)
	}

	val updateNotification = Command("updatenotification") {
		UpdateNotifier.sendUpdateNotification()
	}
}