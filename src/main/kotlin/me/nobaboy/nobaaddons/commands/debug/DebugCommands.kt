package me.nobaboy.nobaaddons.commands.debug

import com.mojang.brigadier.context.CommandContext
import dev.celestialfault.commander.annotations.Command
import dev.celestialfault.commander.annotations.EnabledIf
import dev.celestialfault.commander.annotations.Group
import dev.celestialfault.commander.annotations.RootCommand
import kotlinx.coroutines.delay
import me.nobaboy.nobaaddons.api.DebugAPI
import me.nobaboy.nobaaddons.api.HypixelAPI
import me.nobaboy.nobaaddons.api.PartyAPI
import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI
import me.nobaboy.nobaaddons.commands.adapters.FormattingHandler
import me.nobaboy.nobaaddons.commands.impl.Context
import me.nobaboy.nobaaddons.commands.impl.NobaClientCommandGroup
import me.nobaboy.nobaaddons.core.DebugFlag
import me.nobaboy.nobaaddons.core.PersistentCache
import me.nobaboy.nobaaddons.core.UpdateNotifier
import me.nobaboy.nobaaddons.core.profile.ProfileData
import me.nobaboy.nobaaddons.utils.ErrorManager
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.NobaColor.Companion.toNobaColor
import me.nobaboy.nobaaddons.utils.StringUtils
import me.nobaboy.nobaaddons.utils.TextUtils.buildText
import me.nobaboy.nobaaddons.utils.TextUtils.toText
import me.nobaboy.nobaaddons.utils.annotations.UntranslatedMessage
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import me.nobaboy.nobaaddons.utils.chat.Message
import me.nobaboy.nobaaddons.utils.render.EntityOverlay
import me.nobaboy.nobaaddons.utils.render.EntityOverlay.highlight
import me.nobaboy.nobaaddons.utils.sound.SoundUtils
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.entity.LivingEntity
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Formatting

@OptIn(UntranslatedMessage::class)
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
	fun dev() {
		PersistentCache.devMode = !PersistentCache.devMode
		if(PersistentCache.devMode) {
			ChatUtils.addMessage("Enabled developer mode; use Right Control while hovering over an item to copy it")
		} else {
			ChatUtils.addMessage("Disabled developer mode")
		}
	}

	@Group
	object Party {
		@RootCommand
		fun list() {
			PartyAPI.listMembers()
		}

		@Command
		fun refresh() {
			PartyAPI.refreshPartyList()
			ChatUtils.addMessage("Marked party data as needing update")
		}
	}

	@Group
	object Sounds {
		@RootCommand
		@EnabledIf(DebugAPI.RequiresAWT::class)
		fun root() {
			DebugAPI.openSoundDebugMenu()
		}

		@Command
		fun rareDrop() {
			SoundUtils.rareDropSound.play()
		}

		@Command
		fun zeldaSecret() {
			SoundUtils.zeldaSecretSound.play()
		}
	}

	@Command
	fun location(ctx: Context) {
		ctx.dumpInfo(
			"Server" to HypixelAPI.serverName,
			"Type" to HypixelAPI.serverType,
			"Lobby" to HypixelAPI.lobbyName,
			"Mode" to HypixelAPI.mode,
			"Map" to HypixelAPI.map,
			"Detected Island" to SkyBlockAPI.currentIsland,
			"Zone" to SkyBlockAPI.currentZone,
		)
	}

	@Command
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
	fun clearSuppressedErrors(ctx: Context) {
		ErrorManager.clearPreviousErrors()
		ctx.source.sendFeedback("ok".toText())
	}

	@Command
	suspend fun sendAsyncChatMessage() {
		delay(0L) // just to ensure that kotlin doesn't try to optimize the suspend out
		ChatUtils.addMessage("boat goes binted")
	}

	@Command
	fun updateNotification() {
		UpdateNotifier.sendUpdateNotification()
	}

	@Command
	fun flushCaches() {
		PersistentCache.save()
		ProfileData.saveAll()
	}

	@Command
	fun profile(ctx: Context) {
		ctx.dumpInfo(
			"Current Profile" to ProfileData.PROFILE.profile,
			"Profile Type" to SkyBlockAPI.profileType
		)
	}

	@Command
	fun fake(text: Text) {
		MCUtils.player!!.sendMessage(text, false)
	}

	@Command
	fun flag(flag: DebugFlag) {
		flag.enabled = !flag.enabled
		ChatUtils.addMessage("$flag is now ${if(flag.enabled) "enabled" else "disabled"}")
	}

	@Command
	fun overlay(ctx: Context, formatting: @FormattingHandler.ColorOnly Formatting? = null) {
		val entity = MCUtils.client.targetedEntity as? LivingEntity ?: ctx.source.player
		if(formatting == null) {
			EntityOverlay.remove(entity)
			return
		}

		entity.highlight(formatting.toNobaColor())
	}

	private var lastResponse: Message? = null

	@Command
	fun removeLastResponse() {
		lastResponse?.remove()
		lastResponse = ChatUtils.addAndCaptureMessage("Debug message ${StringUtils.randomAlphanumeric()}".toText())
	}

	val mayor = NobaClientCommandGroup(MayorDebugCommands)
	val item = NobaClientCommandGroup(ItemDebugCommands)
	val pet = NobaClientCommandGroup(PetDebugCommands)
	val regex = NobaClientCommandGroup(RepoDebugCommands)
	val hud = NobaClientCommandGroup(HudDebugCommands)
}