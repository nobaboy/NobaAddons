package me.nobaboy.nobaaddons.features.slayers

import me.nobaboy.nobaaddons.api.skyblock.SlayerAPI
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.events.impl.client.SoundEvents
import me.nobaboy.nobaaddons.events.impl.skyblock.SlayerEvents
import me.nobaboy.nobaaddons.utils.Timestamp
import me.nobaboy.nobaaddons.utils.render.EntityOverlay.highlight
import me.nobaboy.nobaaddons.utils.render.RenderUtils
import me.nobaboy.nobaaddons.utils.sound.SoundUtils
import me.nobaboy.nobaaddons.utils.tr
import net.minecraft.util.Identifier
import kotlin.time.Duration.Companion.seconds

object MiniBossFeatures {
	private val config get() = NobaConfig.slayers
	private val enabled: Boolean get() = SlayerAPI.currentQuest?.spawned == false

	private val EXPLODE = Identifier.ofVanilla("entity.generic.explode")

	private var lastAlert = Timestamp.distantPast()

	fun init() {
		SoundEvents.SOUND.register(this::onSound)
		SlayerEvents.MINI_BOSS_SPAWN.register(this::onMiniBossSpawn)
	}

	private fun onSound(event: SoundEvents.Sound) {
		if(!config.miniBossAlert.enabled || !enabled) return
		if(lastAlert.elapsedSince() < 1.seconds) return
		if(event.id != EXPLODE || event.volume != 0.6f || event.pitch != 9 / 7f) return

		RenderUtils.drawTitle(tr("nobaaddons.slayers.miniBossAlert.spawned", "MiniBoss Spawned!"), config.miniBossAlert.alertColor, duration = 1.5.seconds, id = "slayer_alert")
		SoundUtils.dingLowSound.play()
		lastAlert = Timestamp.now()
	}

	private fun onMiniBossSpawn(event: SlayerEvents.MiniBossSpawn) {
		if(!config.highlightMiniBosses.enabled || !enabled) return
		event.entity.highlight(config.highlightMiniBosses.highlightColor)
	}
}