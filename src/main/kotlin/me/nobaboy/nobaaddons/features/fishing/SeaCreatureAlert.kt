package me.nobaboy.nobaaddons.features.fishing

import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.core.fishing.SeaCreature
import me.nobaboy.nobaaddons.events.impl.skyblock.FishingEvents
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.TextUtils.bold
import me.nobaboy.nobaaddons.utils.TextUtils.yellow
import me.nobaboy.nobaaddons.utils.render.RenderUtils
import me.nobaboy.nobaaddons.utils.tr
import me.owdding.ktmodules.Module

@Module
object SeaCreatureAlert {
	private val config get() = NobaConfig.fishing.seaCreatureAlert
	private val enabled: Boolean get() = config.enabled && SkyBlockAPI.inSkyBlock

	private val SeaCreature.isRare: Boolean
		get() = rarity >= config.minimumRarity || (id == "CARROT_KING" && config.carrotKing) || (id == "NUTCRACKER" && config.nutcracker)

	init {
		FishingEvents.SEA_CREATURE_CATCH.register(this::onSeaCreatureCatch)
	}

	private fun onSeaCreatureCatch(event: FishingEvents.SeaCreatureCatch) {
		if(!enabled) return

		val seaCreature = event.seaCreature
		if(!seaCreature.isRare) return

		val text = if(config.nameInsteadOfRarity) {
			"${seaCreature.displayName}!"
		} else {
			"${seaCreature.rarity} Catch!"
		}

		val subtext = if(event.doubleHook) {
			tr("nobaaddons.fishing.doubleHook.prefix", "DOUBLE HOOK!").yellow().bold()
		} else {
			null
		}

		val color = seaCreature.rarity.color ?: NobaColor.RED

		RenderUtils.drawTitle(text, color, subtext = subtext, id = "sea_creature_alert")
		config.notificationSound.play()
	}
}