package me.nobaboy.nobaaddons.features.slayers

import me.nobaboy.nobaaddons.api.skyblock.SlayerAPI
import me.nobaboy.nobaaddons.config.option.booleanController
import me.nobaboy.nobaaddons.config.option.colorController
import me.nobaboy.nobaaddons.events.impl.client.SoundEvents
import me.nobaboy.nobaaddons.events.impl.skyblock.SlayerEvents
import me.nobaboy.nobaaddons.features.Feature
import me.nobaboy.nobaaddons.features.FeatureCategory
import me.nobaboy.nobaaddons.utils.CommonText
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.Timestamp
import me.nobaboy.nobaaddons.utils.render.EntityOverlay.highlight
import me.nobaboy.nobaaddons.utils.render.RenderUtils
import me.nobaboy.nobaaddons.utils.sound.SoundUtils
import me.nobaboy.nobaaddons.utils.tr
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import kotlin.time.Duration.Companion.seconds

object MiniBossFeatures : Feature("slayerMiniBosses", tr("nobaaddons.feature.slayerMiniBosses", "Mini-Bosses"), FeatureCategory.SLAYER) {
	private val bossIsSpawned: Boolean get() = SlayerAPI.currentQuest?.spawned == true

	@Order(0)
	private var spawnAlert by config(false) {
		name = tr("nobaaddons.config.slayers.miniBosses.spawnAlert", "Alert on Spawn")
		booleanController()
	}

	@Order(1)
	private var alertColor by config(NobaColor.RED) {
		name = CommonText.Config.ALERT_COLOR
		colorController()
	}

	@Order(2)
	private var highlight by config(false) {
		name = tr("nobaaddons.config.slayers.miniBosses.highlight", "Highlight MiniBosses")
		description {
			if(FabricLoader.getInstance().isModLoaded("iris")) {
				CommonText.Config.ENTITY_OVERLAY_IRIS_CONFLICT
			} else {
				Text.empty()
			}
		}
		booleanController()
	}

	@Order(3)
	private var highlightColor by config(NobaColor.GOLD) {
		name = CommonText.Config.HIGHLIGHT_COLOR
		colorController()
		requires { option(::highlight) }
	}

	private val EXPLODE = Identifier.ofVanilla("entity.generic.explode")

	private var lastAlert = Timestamp.distantPast()

	override fun init() {
		listen(SoundEvents.SOUND, listener = this::onSound)
		listen(SlayerEvents.MINI_BOSS_SPAWN, listener = this::onMiniBossSpawn)
	}

	private fun onSound(event: SoundEvents.Sound) {
		if(!spawnAlert || bossIsSpawned) return
		if(lastAlert.elapsedSince() < 1.seconds) return
		if(event.id != EXPLODE || event.volume != 0.6f || event.pitch != 9 / 7f) return

		RenderUtils.drawTitle(tr("nobaaddons.slayers.miniBossAlert.spawned", "MiniBoss Spawned!"), alertColor, duration = 1.5.seconds, id = "slayer.alert")
		SoundUtils.dingLowSound.play()
		lastAlert = Timestamp.now()
	}

	private fun onMiniBossSpawn(event: SlayerEvents.MiniBossSpawn) {
		if(!highlight || bossIsSpawned) return
		event.entity.highlight(highlightColor)
	}
}