package me.nobaboy.nobaaddons

import com.google.gson.Gson
import com.mojang.logging.LogUtils
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import me.nobaboy.nobaaddons.api.DebugAPI
import me.nobaboy.nobaaddons.api.InventoryAPI
import me.nobaboy.nobaaddons.api.PartyAPI
import me.nobaboy.nobaaddons.api.skyblock.DungeonsAPI
import me.nobaboy.nobaaddons.api.skyblock.MayorAPI
import me.nobaboy.nobaaddons.api.skyblock.PetAPI
import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI
import me.nobaboy.nobaaddons.api.skyblock.SlayerAPI
import me.nobaboy.nobaaddons.api.skyblock.TrophyFishAPI
import me.nobaboy.nobaaddons.api.skyblock.mythological.BurrowAPI
import me.nobaboy.nobaaddons.api.skyblock.mythological.BurrowGuessAPI
import me.nobaboy.nobaaddons.api.skyblock.mythological.DianaAPI
import me.nobaboy.nobaaddons.commands.NobaCommand
import me.nobaboy.nobaaddons.commands.SWikiCommand
import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.data.PersistentCache
import me.nobaboy.nobaaddons.features.chat.alerts.IAlert
import me.nobaboy.nobaaddons.features.chat.chatcommands.impl.DMCommands
import me.nobaboy.nobaaddons.features.chat.chatcommands.impl.GuildCommands
import me.nobaboy.nobaaddons.features.chat.chatcommands.impl.PartyCommands
import me.nobaboy.nobaaddons.features.chat.filters.IChatFilter
import me.nobaboy.nobaaddons.features.dungeons.HighlightStarredMobs
import me.nobaboy.nobaaddons.features.dungeons.SimonSaysTimer
import me.nobaboy.nobaaddons.features.events.mythological.AnnounceRareDrops
import me.nobaboy.nobaaddons.features.events.mythological.BurrowWaypoints
import me.nobaboy.nobaaddons.features.events.mythological.InquisitorWaypoints
import me.nobaboy.nobaaddons.features.fishing.FishingBobberTweaks
import me.nobaboy.nobaaddons.features.fishing.HighlightThunderSparks
import me.nobaboy.nobaaddons.features.fishing.SeaCreatureAlert
import me.nobaboy.nobaaddons.features.fishing.TrophyFishChat
import me.nobaboy.nobaaddons.features.mining.glacitemineshaft.CorpseLocator
import me.nobaboy.nobaaddons.features.mining.glacitemineshaft.MineshaftWaypoints
import me.nobaboy.nobaaddons.features.qol.MouseLock
import me.nobaboy.nobaaddons.features.qol.sound.filters.ISoundFilter
import me.nobaboy.nobaaddons.features.slayers.AnnounceBossKillTime
import me.nobaboy.nobaaddons.features.slayers.MiniBossAlert
import me.nobaboy.nobaaddons.features.slayers.voidgloom.VoidgloomFeatures
import me.nobaboy.nobaaddons.features.visuals.EtherwarpHelper
import me.nobaboy.nobaaddons.features.visuals.TemporaryWaypoint
import me.nobaboy.nobaaddons.features.visuals.slotinfo.ISlotInfo
import me.nobaboy.nobaaddons.repo.RepoManager
import me.nobaboy.nobaaddons.screens.hud.ElementManager
import me.nobaboy.nobaaddons.screens.infoboxes.InfoBoxesManager
import me.nobaboy.nobaaddons.screens.keybinds.KeyBindsManager
import me.nobaboy.nobaaddons.utils.CommonText
import me.nobaboy.nobaaddons.utils.TextUtils.buildText
import me.nobaboy.nobaaddons.utils.TextUtils.literal
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import org.slf4j.Logger
import java.nio.file.Path

object NobaAddons : ClientModInitializer {
	const val MOD_ID = "nobaaddons"
	val VERSION: String = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow().metadata.version.friendlyString

	val PREFIX: Text get() = buildText {
		append(CommonText.NOBAADDONS)
		literal(" » ") { formatted(Formatting.DARK_GRAY) }
		formatted(Formatting.BLUE, Formatting.BOLD)
	}

	val LOGGER: Logger = LogUtils.getLogger()
	val CONFIG_DIR: Path get() = FabricLoader.getInstance().configDir.resolve(MOD_ID)

	val GSON = Gson()

	@OptIn(ExperimentalSerializationApi::class)
	val JSON = Json {
		ignoreUnknownKeys = true
		allowStructuredMapKeys = true

		// allow some quality of life
		allowComments = true
		allowTrailingComma = true

		// encoding related
		encodeDefaults = true
		prettyPrint = true
	}

	private val supervisorJob = SupervisorJob()
	val coroutineScope = CoroutineScope(CoroutineName(MOD_ID) + supervisorJob)

	fun runAsync(runnable: suspend CoroutineScope.() -> Unit) = coroutineScope.launch(block = runnable)

	// Note: utility object classes should avoid calling a dedicated `init` method here where possible, and instead
	// rely on 'init {}' to run setup when first used, unless absolutely necessary for functionality (such as
	// if the object class is never referenced anywhere else, or if it relies on chat data for a feature that isn't
	// immediately ran).
	override fun onInitializeClient() {
		NobaConfigManager.init()
		PersistentCache.init()
		RepoManager.init()

		/* region APIs */
		InventoryAPI.init()
		PartyAPI.init()
		SkyBlockAPI.init()
		DebugAPI.init()
		MayorAPI.init()
		PetAPI.init()
		SlayerAPI.init()
		DungeonsAPI.init()
		DianaAPI.init()
		BurrowAPI.init()
		BurrowGuessAPI.init()
		TrophyFishAPI.init()
		/* endregion */

		/* region Screens */
		InfoBoxesManager.init()
		KeyBindsManager.init()
		/* endregion */

		/* region Commands */
		NobaCommand.init()
		SWikiCommand.init()
		/* endregion */

		/* region User Interface */
		ElementManager.init()
		/* endregion */

		/* region Features */
		// region Visuals
		TemporaryWaypoint.init()
		EtherwarpHelper.init()
		ISlotInfo.init()
		// endregion

		// region Events
		AnnounceRareDrops.init()
		BurrowWaypoints.init()
		InquisitorWaypoints.init()
		// endregion

		// region Slayers
		AnnounceBossKillTime.init()
		MiniBossAlert.init()
		VoidgloomFeatures.init()
		// endregion

		// region Fishing
		FishingBobberTweaks.init()
		SeaCreatureAlert.init()
		TrophyFishChat.init()
		// endregion

		// region Mining
		CorpseLocator.init()
		MineshaftWaypoints.init()
		// endregion

		// region Crimson Isle
		HighlightThunderSparks.init()
		// endregion

		// region Dungeons
		HighlightStarredMobs.init()
		SimonSaysTimer.init()
		// endregion

		// region Chat
		IAlert.init()
		IChatFilter.init()
		// endregion

		// region Chat Commands
		DMCommands.init()
		PartyCommands.init()
		GuildCommands.init()
		// endregion

		// region QOL
		ISoundFilter.init()
		MouseLock.init()
		// endregion
		/* endregion */
	}
}