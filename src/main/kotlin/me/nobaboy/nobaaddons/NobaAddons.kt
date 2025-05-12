package me.nobaboy.nobaaddons

import com.mojang.logging.LogUtils
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import me.nobaboy.nobaaddons.api.DebugAPI
import me.nobaboy.nobaaddons.api.InventoryAPI
import me.nobaboy.nobaaddons.api.PartyAPI
import me.nobaboy.nobaaddons.api.skyblock.DungeonsAPI
import me.nobaboy.nobaaddons.api.skyblock.MayorAPI
import me.nobaboy.nobaaddons.api.skyblock.PetAPI
import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI
import me.nobaboy.nobaaddons.api.skyblock.SlayerAPI
import me.nobaboy.nobaaddons.api.skyblock.events.mythological.BurrowAPI
import me.nobaboy.nobaaddons.api.skyblock.fishing.SeaCreatureAPI
import me.nobaboy.nobaaddons.api.skyblock.fishing.TrophyFishAPI
import me.nobaboy.nobaaddons.commands.NobaCommand
import me.nobaboy.nobaaddons.commands.SWikiCommand
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.config.UISettings
import me.nobaboy.nobaaddons.config.util.safeLoad
import me.nobaboy.nobaaddons.core.PersistentCache
import me.nobaboy.nobaaddons.core.UpdateNotifier
import me.nobaboy.nobaaddons.features.chat.CopyChatFeature
import me.nobaboy.nobaaddons.features.chat.channeldisplay.ChatChannelDisplay
import me.nobaboy.nobaaddons.features.chat.chatcommands.impl.DMCommands
import me.nobaboy.nobaaddons.features.chat.chatcommands.impl.GuildCommands
import me.nobaboy.nobaaddons.features.chat.chatcommands.impl.PartyCommands
import me.nobaboy.nobaaddons.features.chat.filters.IChatFilter
import me.nobaboy.nobaaddons.features.chat.notifications.ChatNotifications
import me.nobaboy.nobaaddons.features.chat.notifications.ChatNotificationsConfig
import me.nobaboy.nobaaddons.features.chocolatefactory.ChocolateFactoryFeatures
import me.nobaboy.nobaaddons.features.crimsonisle.AnnounceVanquisher
import me.nobaboy.nobaaddons.features.dungeons.HighlightStarredMobs
import me.nobaboy.nobaaddons.features.dungeons.SimonSaysTimer
import me.nobaboy.nobaaddons.features.events.hoppity.HoppityEggGuess
import me.nobaboy.nobaaddons.features.events.mythological.AnnounceRareDrops
import me.nobaboy.nobaaddons.features.events.mythological.BurrowWaypoints
import me.nobaboy.nobaaddons.features.events.mythological.GriffinBurrowGuess
import me.nobaboy.nobaaddons.features.events.mythological.InquisitorWaypoints
import me.nobaboy.nobaaddons.features.fishing.AnnounceSeaCreatures
import me.nobaboy.nobaaddons.features.fishing.CatchTimer
import me.nobaboy.nobaaddons.features.fishing.FishingBobberTweaks
import me.nobaboy.nobaaddons.features.fishing.FixFishHookFieldDesync
import me.nobaboy.nobaaddons.features.fishing.HotspotWaypoints
import me.nobaboy.nobaaddons.features.fishing.RevertTreasureMessages
import me.nobaboy.nobaaddons.features.fishing.SeaCreatureAlert
import me.nobaboy.nobaaddons.features.fishing.crimsonisle.HighlightThunderSparks
import me.nobaboy.nobaaddons.features.fishing.crimsonisle.TrophyFishChat
import me.nobaboy.nobaaddons.features.inventory.ItemPickupLog
import me.nobaboy.nobaaddons.features.inventory.enchants.EnchantmentTooltips
import me.nobaboy.nobaaddons.features.inventory.slotinfo.ISlotInfo
import me.nobaboy.nobaaddons.features.keybinds.KeyBindsManager
import me.nobaboy.nobaaddons.features.mining.WormAlert
import me.nobaboy.nobaaddons.features.mining.glacitemineshaft.CorpseLocator
import me.nobaboy.nobaaddons.features.mining.glacitemineshaft.MineshaftWaypoints
import me.nobaboy.nobaaddons.features.qol.MouseLock
import me.nobaboy.nobaaddons.features.qol.sound.filters.ISoundFilter
import me.nobaboy.nobaaddons.features.rift.RiftTimers
import me.nobaboy.nobaaddons.features.slayers.CompactSlayerMessages
import me.nobaboy.nobaaddons.features.slayers.MiniBossFeatures
import me.nobaboy.nobaaddons.features.slayers.SlayerBossFeatures
import me.nobaboy.nobaaddons.features.slayers.inferno.HighlightHellionShield
import me.nobaboy.nobaaddons.features.slayers.sven.HidePupNametags
import me.nobaboy.nobaaddons.features.slayers.voidgloom.VoidgloomSeraphFeatures
import me.nobaboy.nobaaddons.features.ui.infobox.InfoBoxesConfig
import me.nobaboy.nobaaddons.features.visuals.EtherwarpOverlay
import me.nobaboy.nobaaddons.features.visuals.TemporaryWaypoints
import me.nobaboy.nobaaddons.repo.RepoManager
import me.nobaboy.nobaaddons.ui.UIManager
import me.nobaboy.nobaaddons.utils.CommonText
import me.nobaboy.nobaaddons.utils.ErrorManager
import me.nobaboy.nobaaddons.utils.TextUtils.blue
import me.nobaboy.nobaaddons.utils.TextUtils.bold
import me.nobaboy.nobaaddons.utils.TextUtils.buildText
import me.nobaboy.nobaaddons.utils.TextUtils.darkGray
import me.nobaboy.nobaaddons.utils.TextUtils.literal
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.Version
import net.minecraft.text.Text
import org.slf4j.Logger
import java.nio.file.Path

object NobaAddons : ClientModInitializer {
	const val MOD_ID = "nobaaddons"

	val VERSION_INFO: Version = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow().metadata.version
	val VERSION: String = VERSION_INFO.friendlyString

	val PREFIX: Text get() = buildText {
		append(if(NobaConfig.general.compactModMessagePrefix) CommonText.NOBA else CommonText.NOBAADDONS)
		literal(" » ") { darkGray() }
		blue().bold()
	}

	val LOGGER: Logger = LogUtils.getLogger()
	val CONFIG_DIR: Path get() = FabricLoader.getInstance().configDir.resolve(MOD_ID)

	val JSON = Json {
		ignoreUnknownKeys = true
		encodeDefaults = true
		prettyPrint = true
	}

	private val supervisorJob = SupervisorJob()
	private val exceptionHandler = CoroutineExceptionHandler { _, error ->
		ErrorManager.logError("Encountered an unhandled error in an async context", error)
	}
	val coroutineScope = CoroutineScope(CoroutineName(MOD_ID) + supervisorJob + exceptionHandler)

	fun runAsync(runnable: suspend CoroutineScope.() -> Unit) = coroutineScope.launch(block = runnable)

	override fun onInitializeClient() {
		loadConfigs()
		initCore()
		initApis()
		initFeatures()
	}

	private fun loadConfigs() {
		NobaConfig.safeLoad()
		PersistentCache.safeLoad()
		UISettings.safeLoad()
		ChatNotificationsConfig.safeLoad()
		InfoBoxesConfig.safeLoad()
	}

	private fun initCore() {
		RepoManager
		UIManager
	}

	private fun initApis() {
		BurrowAPI
		DebugAPI
		DungeonsAPI
		InventoryAPI
		MayorAPI
		PartyAPI
		PetAPI
		SeaCreatureAPI
		SkyBlockAPI
		SlayerAPI
		TrophyFishAPI
	}

	private fun initFeatures() {
		// region Core
		UpdateNotifier
		// endregion

		// region Commands
		NobaCommand
		SWikiCommand
		// endregion

		// region Visuals
		EtherwarpOverlay
		TemporaryWaypoints
		// endregion

		// region User Interface
		ItemPickupLog
		// endregion

		// region Inventory
		EnchantmentTooltips
		ISlotInfo
		// endregion

		// region Events
		/* region Hoppity */
		HoppityEggGuess
		ChocolateFactoryFeatures
		/* endregion */

		/* region Mythological */
		AnnounceRareDrops
		GriffinBurrowGuess
		BurrowWaypoints
		InquisitorWaypoints
		/* endregion */
		// endregion

		// region Slayers
		MiniBossFeatures
		SlayerBossFeatures
		CompactSlayerMessages
		/* region Sven Packmaster */
		HidePupNametags
		/* endregion */
		/* region Voidgloom Seraph */
		VoidgloomSeraphFeatures
		/* endregion */
		/* region Inferno Demonlord */
		HighlightHellionShield
		/* endregion */
		// endregion

		// region Fishing
		FixFishHookFieldDesync
		AnnounceSeaCreatures
		CatchTimer
		FishingBobberTweaks
		HighlightThunderSparks
		HotspotWaypoints
		RevertTreasureMessages
		SeaCreatureAlert
		TrophyFishChat
		// endregion

		// region Mining
		CorpseLocator
		MineshaftWaypoints
		WormAlert
		// endregion

		// region Crimson Isle
		AnnounceVanquisher
		// endregion

		// region Dungeons
		HighlightStarredMobs
		SimonSaysTimer
		// endregion

		// region Chat
		CopyChatFeature
		ChatNotifications
		IChatFilter
		ChatChannelDisplay
		KeyBindsManager
		/* region Chat Commands */
		DMCommands
		PartyCommands
		GuildCommands
		/* endregion */
		// endregion

		// region QOL
		ISoundFilter
		MouseLock
		// endregion

		// region Rift
		RiftTimers
		// endregion
	}
}