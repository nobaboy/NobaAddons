package me.nobaboy.nobaaddons

import com.mojang.logging.LogUtils
import kotlinx.coroutines.*
import me.nobaboy.nobaaddons.api.DungeonsAPI
import me.nobaboy.nobaaddons.api.PartyAPI
import me.nobaboy.nobaaddons.api.SkyBlockAPI
import me.nobaboy.nobaaddons.commands.NobaCommand
import me.nobaboy.nobaaddons.commands.SWikiCommand
import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.config.ui.ElementManager
import me.nobaboy.nobaaddons.features.chat.alerts.IAlert
import me.nobaboy.nobaaddons.features.chat.chatcommands.impl.DMCommands
import me.nobaboy.nobaaddons.features.chat.chatcommands.impl.GuildCommands
import me.nobaboy.nobaaddons.features.chat.chatcommands.impl.PartyCommands
import me.nobaboy.nobaaddons.features.chat.filters.IFilter
import me.nobaboy.nobaaddons.features.crimsonisle.HighlightThunderSparks
import me.nobaboy.nobaaddons.features.dungeons.HighlightStarredMobs
import me.nobaboy.nobaaddons.features.dungeons.SimonSaysTimer
import me.nobaboy.nobaaddons.features.visuals.TemporaryWaypoint
import me.nobaboy.nobaaddons.features.visuals.itemoverlays.EtherwarpHelper
import me.nobaboy.nobaaddons.utils.ModAPIUtils.listen
import me.nobaboy.nobaaddons.utils.ModAPIUtils.subscribeToEvent
import me.nobaboy.nobaaddons.utils.Scheduler
import me.nobaboy.nobaaddons.utils.Utils
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import me.nobaboy.nobaaddons.utils.keybinds.KeyBindListener
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.hypixel.modapi.HypixelModAPI
import net.hypixel.modapi.packet.impl.clientbound.ClientboundPingPacket
import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket
import net.minecraft.text.MutableText
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import org.slf4j.Logger
import java.nio.file.Path

object NobaAddons : ClientModInitializer {
	const val MOD_ID = "nobaaddons"
	val VERSION = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow().metadata.version.friendlyString

	val PREFIX: MutableText
		get() = Text.empty()
			.append(Text.translatable("nobaaddons.name")
			.append(Text.literal(" > ")).setStyle(
				Style.EMPTY.withColor(Formatting.BLUE).withBold(true))
			)

	val LOGGER: Logger = LogUtils.getLogger()
	val modDir: Path get() = FabricLoader.getInstance().configDir

	private val supervisorJob = SupervisorJob()
	private val coroutineScope = CoroutineScope(CoroutineName(MOD_ID) + supervisorJob)

	fun runAsync(runnable: suspend CoroutineScope.() -> Unit) = coroutineScope.launch(block = runnable)

	override fun onInitializeClient() {
		NobaConfigManager.init()

		// APIs
		PartyAPI.init()
		DungeonsAPI.init()
		Scheduler.schedule(20, repeat = true) { SkyBlockAPI.update() }

		// Utils
		KeyBindListener.init()
		Scheduler.schedule(20, repeat = true) { ChatUtils.tickCommandQueue() }

		// Commands
		NobaCommand.init()
		SWikiCommand.init()

		// User Interface
		ElementManager.init()

		// Features

		// Visuals
		TemporaryWaypoint.init()
		EtherwarpHelper.init()

		// Chat
		IAlert.init()
		IFilter.init()

		// Chat Commands
		DMCommands.init()
		PartyCommands.init()
		GuildCommands.init()

		// Crimson Isle
		HighlightThunderSparks.init()

		// Dungeons
		SimonSaysTimer.init()
		HighlightStarredMobs.init()

		HypixelModAPI.getInstance().subscribeToEvent<ClientboundLocationPacket>()
		HypixelModAPI.getInstance().listen<ClientboundLocationPacket>(SkyBlockAPI::onLocationPacket)

		Scheduler.schedule(60 * 20, repeat = true) { Utils.sendPingPacket() }
		HypixelModAPI.getInstance().listen<ClientboundPingPacket>(Utils::onPingPacket)
	}
}
