package me.nobaboy.nobaaddons.commands.debug

import com.mojang.brigadier.context.CommandContext
import me.nobaboy.nobaaddons.api.DebugAPI
import me.nobaboy.nobaaddons.api.PartyAPI
import me.nobaboy.nobaaddons.api.skyblock.MayorAPI
import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI
import me.nobaboy.nobaaddons.commands.impl.AnnotatedGroup
import me.nobaboy.nobaaddons.commands.annotations.Command
import me.nobaboy.nobaaddons.commands.impl.Context
import me.nobaboy.nobaaddons.commands.annotations.EnabledIf
import me.nobaboy.nobaaddons.commands.annotations.Group
import me.nobaboy.nobaaddons.core.UpdateNotifier
import me.nobaboy.nobaaddons.core.mayor.Mayor
import me.nobaboy.nobaaddons.ui.TextHudElement
import me.nobaboy.nobaaddons.ui.UIManager
import me.nobaboy.nobaaddons.ui.data.TextElement
import me.nobaboy.nobaaddons.utils.ErrorManager
import me.nobaboy.nobaaddons.utils.TextUtils.buildText
import me.nobaboy.nobaaddons.utils.TextUtils.toText
import me.nobaboy.nobaaddons.utils.annotations.UntranslatedMessage
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Colors
import net.minecraft.util.Formatting
import kotlin.jvm.optionals.getOrNull

@Suppress("unused")
@Group("debug")
object DebugCommands {
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

	@Command
	fun party(ctx: Context) {
		PartyAPI.listMembers()
	}

	@Command
	fun mayor(ctx: Context) {
		val mayor = MayorAPI.currentMayor
		val minister = MayorAPI.currentMinister

		if(mayor == Mayor.UNKNOWN && minister == Mayor.UNKNOWN) {
			ctx.source.sendError(Text.literal("Current Mayor and Minister are still unknown"))
			return
		}

		ctx.dumpInfo(
			"Current Mayor" to mayor.mayorName,
			"Mayor Perks" to mayor.activePerks,
			"Current Minister" to minister.mayorName,
			"Minister Perk" to minister.activePerks,
		)
	}

	@Command
	@EnabledIf(DebugAPI.RequiresAWT::class)
	fun sounds(ctx: Context) {
		DebugAPI.openSoundDebugMenu()
	}

	@Command
	fun location(ctx: Context) {
		val location = DebugAPI.lastLocationPacket
		ctx.dumpInfo(
			"Server" to location.serverName,
			"Type" to location.serverType.getOrNull(),
			"Lobby" to location.lobbyName.getOrNull(),
			"Mode" to location.mode.getOrNull(),
			"Map" to location.map.getOrNull(),
			"Detected Island" to SkyBlockAPI.currentIsland,
			"Zone" to SkyBlockAPI.currentZone,
		)
	}

	@Command
	@OptIn(UntranslatedMessage::class)
	fun clickAction(ctx: Context) {
		ChatUtils.addMessageWithClickAction("Click me!") { ChatUtils.addMessage("You clicked me!") }
	}

	@Command
	fun error(ctx: Context) {
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

	@Command
	fun updateNotification(ctx: Context) {
		UpdateNotifier.sendUpdateNotification()
	}

	@Command
	fun addUiElement(ctx: Context) {
		// TextElement would normally be in an AbstractConfig, but this is a debug command,
		// so we don't care if it doesn't have any persistent position state.
		UIManager.add(object : TextHudElement(TextElement(color = listOf(
			Colors.WHITE, Colors.GREEN, Colors.LIGHT_RED, Colors.GRAY,
			Colors.ALTERNATE_WHITE, Colors.BLUE, Colors.LIGHT_YELLOW,
			Colors.YELLOW
		).random())) {
			override fun renderText(context: DrawContext) {
				renderLine(context, "Zoop!".toText())
			}

			override val name: Text = Text.literal("Debug HUD")
			override val size: Pair<Int, Int> = 100 to 25
		})
	}

	val item = AnnotatedGroup(ItemDebugCommands)
	val pet = AnnotatedGroup(PetDebugCommands)
	val regex = AnnotatedGroup(RepoDebugCommands)
}