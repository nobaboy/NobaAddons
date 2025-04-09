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
import me.nobaboy.nobaaddons.api.skyblock.events.mythological.BurrowGuessAPI
import me.nobaboy.nobaaddons.api.skyblock.events.mythological.DianaAPI
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
import me.nobaboy.nobaaddons.features.chat.chatcommands.impl.DMCommands
import me.nobaboy.nobaaddons.features.chat.chatcommands.impl.GuildCommands
import me.nobaboy.nobaaddons.features.chat.chatcommands.impl.PartyCommands
import me.nobaboy.nobaaddons.features.chat.filters.IChatFilter
import me.nobaboy.nobaaddons.features.chat.notifications.ChatNotifications
import me.nobaboy.nobaaddons.features.chat.notifications.ChatNotificationsManager
import me.nobaboy.nobaaddons.features.chocolatefactory.ChocolateFactoryFeatures
import me.nobaboy.nobaaddons.features.dungeons.HighlightStarredMobs
import me.nobaboy.nobaaddons.features.dungeons.SimonSaysTimer
import me.nobaboy.nobaaddons.features.events.hoppity.HoppityEggGuess
import me.nobaboy.nobaaddons.features.events.mythological.AnnounceRareDrops
import me.nobaboy.nobaaddons.features.events.mythological.BurrowWaypoints
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
import me.nobaboy.nobaaddons.features.ui.infobox.InfoBoxesManager
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

	// Note: utility object classes should avoid calling a dedicated `init` method here where possible, and instead
	// rely on 'init {}' to run setup when first used, unless absolutely necessary for functionality (such as
	// if the object class is never referenced anywhere else, or if it relies on chat data for a feature that isn't
	// immediately ran).
	override fun onInitializeClient() {
		/* region Core */
		NobaConfig.safeLoad()
		PersistentCache.safeLoad()
		RepoManager.init()
		UISettings.safeLoad()
		UIManager.init()

		UpdateNotifier.init()
		/* endregion */

		/* region APIs */
		BurrowAPI.init()
		BurrowGuessAPI.init()
		DebugAPI.init()
		DianaAPI.init()
		DungeonsAPI.init()
		InventoryAPI.init()
		MayorAPI.init()
		PartyAPI.init()
		PetAPI.init()
		SeaCreatureAPI.init()
		SkyBlockAPI.init()
		SlayerAPI.init()
		TrophyFishAPI.init()
		/* endregion */

		/* region Screens */
		ChatNotificationsManager.init()
		InfoBoxesManager.init()
		KeyBindsManager.init()
		/* endregion */

		/* region Commands */
		NobaCommand.init()
		SWikiCommand.init()
		/* endregion */

		/* region User Interface */
		ItemPickupLog.init()
		/* endregion */

		/* region Features */
		// region Visuals
		EtherwarpOverlay.init()
		TemporaryWaypoints.init()
		// endregion

		// region Inventory
		EnchantmentTooltips.init()
		ISlotInfo.init()
		// endregion

		// region Events
		/* region Hoppity */
		HoppityEggGuess.init()
		ChocolateFactoryFeatures.init()
		/* endregion */

		/* region Mythological */
		AnnounceRareDrops.init()
		BurrowWaypoints.init()
		InquisitorWaypoints.init()
		/* endregion*/
		// endregion

		// region Slayers
		MiniBossFeatures.init()
		SlayerBossFeatures.init()
		CompactSlayerMessages.init()
		/* region Sven Packmaster */
		HidePupNametags.init()
		/* endregion */
		/* region Voidgloom Seraph */
		VoidgloomSeraphFeatures.init()
		/* endregion */
		/* region Inferno Demonlord */
		HighlightHellionShield.init()
		/* endregion */
		// endregion

		// region Fishing
		FixFishHookFieldDesync.init()
		AnnounceSeaCreatures.init()
		CatchTimer.init()
		FishingBobberTweaks.init()
		HighlightThunderSparks.init()
		HotspotWaypoints.init()
		RevertTreasureMessages.init()
		SeaCreatureAlert.init()
		TrophyFishChat.init()
		// endregion

		// region Mining
		CorpseLocator.init()
		MineshaftWaypoints.init()
		WormAlert.init()
		// endregion

		// region Dungeons
		HighlightStarredMobs.init()
		SimonSaysTimer.init()
		// endregion

		// region Chat
		CopyChatFeature.init()
		ChatNotifications.init()
		IChatFilter.init()
		/* region Chat Commands */
		DMCommands.init()
		PartyCommands.init()
		GuildCommands.init()
		/* endregion */
		// endregion

		// region QOL
		ISoundFilter.init()
		MouseLock.init()
		// endregion

		// region Rift
		RiftTimers.init()
		// endregion
		/* endregion */
	}
}