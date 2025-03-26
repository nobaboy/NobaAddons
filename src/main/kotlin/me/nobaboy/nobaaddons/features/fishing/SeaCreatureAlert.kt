package me.nobaboy.nobaaddons.features.fishing

import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.events.impl.skyblock.FishingEvents
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.TextUtils.bold
import me.nobaboy.nobaaddons.utils.TextUtils.yellow
import me.nobaboy.nobaaddons.utils.render.RenderUtils
import me.nobaboy.nobaaddons.utils.tr

object SeaCreatureAlert {
	private val config get() = NobaConfig.INSTANCE.fishing.seaCreatureAlert
	private val enabled: Boolean get() = config.enabled && SkyBlockAPI.inSkyBlock

	fun init() {
		FishingEvents.SEA_CREATURE_CATCH.register(this::onSeaCreatureCatch)
	}

	private fun onSeaCreatureCatch(event: FishingEvents.SeaCreatureCatch) {
		if(!enabled) return

		val seaCreature = event.seaCreature
		if(seaCreature.rarity < config.minimumRarity) return

		val text = if(config.nameInsteadOfRarity) {
			"${seaCreature.displayName}!"
		} else {
			"${seaCreature.rarity} Catch!"
		}

		val subtext = if(event.doubleHook) {
			tr("nobaaddons.fishing.seaCreatureAlert.doubleHook", "DOUBLE HOOK!").yellow().bold()
		} else {
			null
		}

		RenderUtils.drawTitle(text, (seaCreature.rarity.color ?: NobaColor.RED), subtext = subtext, id = "sea_creature_alert")
		config.notificationSound.play()
	}
}