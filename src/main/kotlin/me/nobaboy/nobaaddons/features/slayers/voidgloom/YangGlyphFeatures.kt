package me.nobaboy.nobaaddons.features.slayers.voidgloom

import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI.inIsland
import me.nobaboy.nobaaddons.api.skyblock.SlayerAPI
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.core.SkyBlockIsland
import me.nobaboy.nobaaddons.core.slayer.SlayerBoss
import me.nobaboy.nobaaddons.events.EntityEvents
import me.nobaboy.nobaaddons.events.WorldEvents
import me.nobaboy.nobaaddons.events.skyblock.SkyBlockEvents
import me.nobaboy.nobaaddons.utils.EntityUtils
import me.nobaboy.nobaaddons.utils.LocationUtils.distanceToPlayer
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.NobaVec
import me.nobaboy.nobaaddons.utils.Scheduler
import me.nobaboy.nobaaddons.utils.Timestamp
import me.nobaboy.nobaaddons.utils.getNobaVec
import me.nobaboy.nobaaddons.utils.render.RenderUtils
import me.nobaboy.nobaaddons.utils.sound.SoundUtils
import me.nobaboy.nobaaddons.utils.toNobaVec
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.minecraft.block.Blocks
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.decoration.ArmorStandEntity
import net.minecraft.entity.mob.EndermanEntity
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

object YangGlyphFeatures {
	private val config get() = NobaConfig.INSTANCE.slayers.voidgloom
	private val enabled: Boolean
		get() = SkyBlockIsland.THE_END.inIsland() && SlayerAPI.currentQuest?.boss == SlayerBoss.VOIDGLOOM

	private val yangGlyphs = mutableMapOf<NobaVec, Timestamp>()
	private val flyingYangGlyphs = mutableListOf<ArmorStandEntity>()
	val inBeaconPhase: Boolean get() = yangGlyphs.isNotEmpty() || flyingYangGlyphs.isNotEmpty()

	fun init() {
		SkyBlockEvents.ISLAND_CHANGE.register { reset() }
		EntityEvents.SPAWN.register(this::onEntitySpawn)
		WorldEvents.BLOCK_UPDATE.register(this::onBlockUpdate)
		WorldRenderEvents.AFTER_TRANSLUCENT.register(this::onWorldRender)
	}

	private fun onEntitySpawn(event: EntityEvents.Spawn) {
		if(!config.yangGlyphAlert) return
		if(!enabled) return

		Scheduler.schedule(2) {
			val bossEntity = SlayerAPI.currentQuest?.entity ?: return@schedule
			val armorStand = event.entity as? ArmorStandEntity ?: return@schedule
			if(armorStand in flyingYangGlyphs) return@schedule

			val nearbyEntities = EntityUtils.getEntitiesNear<EndermanEntity>(armorStand.getNobaVec(), 3.0)
			if(bossEntity !in nearbyEntities) return@schedule

			val helmet = armorStand.getEquippedStack(EquipmentSlot.HEAD)
			if(helmet.name.string != "Beacon") return@schedule

			flyingYangGlyphs.add(armorStand)
		}
	}

	private fun onBlockUpdate(event: WorldEvents.BlockUpdate) {
		if(!config.yangGlyphAlert) return
		if(!enabled) return

		val location = event.blockPos.toNobaVec()

		when(event.newState.block) {
			Blocks.BEACON -> {
				if(SlayerAPI.currentQuest?.spawned == false) return

				val armorStand = flyingYangGlyphs.firstOrNull { it.getNobaVec().distance(location) < 3 } ?: return
				flyingYangGlyphs.remove(armorStand)
				yangGlyphs[location] = Timestamp.now() + 5.seconds

				RenderUtils.drawTitle("Yang Glyph!", config.yangGlyphAlertColor, duration = 1.5.seconds)
				SoundUtils.plingSound.play()
			}
			else -> yangGlyphs.remove(location)
		}
	}

	private fun onWorldRender(context: WorldRenderContext) {
		if(!config.highlightYangGlyphs) return

		yangGlyphs.forEach { (location, timestamp) ->
			if(location.distanceToPlayer() > 20) return@forEach

			val seconds = timestamp.timeRemaining().toString(DurationUnit.SECONDS, 1)
			RenderUtils.renderOutlinedFilledBox(context, location, config.yangGlyphHighlightColor, throughBlocks = true)
			RenderUtils.renderText(location.center().raise(), "Yang Glyph", config.yangGlyphHighlightColor, yOffset = -10f, throughBlocks = true)
			RenderUtils.renderText(location.center().raise(), seconds, NobaColor.WHITE, throughBlocks = true)
		}
	}

	private fun reset() {
		yangGlyphs.clear()
		flyingYangGlyphs.clear()
	}
}