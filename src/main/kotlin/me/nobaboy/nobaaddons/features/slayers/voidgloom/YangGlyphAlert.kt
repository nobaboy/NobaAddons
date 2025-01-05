package me.nobaboy.nobaaddons.features.slayers.voidgloom

import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI.inIsland
import me.nobaboy.nobaaddons.api.skyblock.SlayerAPI
import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.core.SkyBlockIsland
import me.nobaboy.nobaaddons.core.slayer.SlayerBoss
import me.nobaboy.nobaaddons.events.EntityRenderEvents
import me.nobaboy.nobaaddons.events.WorldEvents
import me.nobaboy.nobaaddons.events.skyblock.SkyBlockEvents
import me.nobaboy.nobaaddons.utils.EntityUtils
import me.nobaboy.nobaaddons.utils.LocationUtils.distanceToPlayer
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.NobaColor.Companion.toNobaColor
import me.nobaboy.nobaaddons.utils.NobaVec
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

object YangGlyphAlert {
	private val config get() = NobaConfigManager.config.slayers.voidgloom
	private val enabled: Boolean
		get() = SkyBlockIsland.THE_END.inIsland() && SlayerAPI.currentQuest?.boss == SlayerBoss.VOIDGLOOM

	private val yangGlyphs = mutableMapOf<NobaVec, Timestamp>()
	private val flyingYangGlyphs = mutableListOf<ArmorStandEntity>()

	fun init() {
		SkyBlockEvents.ISLAND_CHANGE.register { reset() }
		WorldEvents.BLOCK_UPDATE.register(this::onBlockUpdate)
		EntityRenderEvents.POST_RENDER.register(this::onEntityRender)
		WorldRenderEvents.AFTER_TRANSLUCENT.register(this::onWorldRender)
	}

	private fun onBlockUpdate(event: WorldEvents.BlockUpdate) {
		if(!config.yangGlyphAlert) return
		if(!enabled) return

		val location = event.blockPos.toNobaVec()

		if(event.newState.block == Blocks.BEACON) {
			if(SlayerAPI.currentQuest?.spawned == false) return

			val armorStand = flyingYangGlyphs.firstOrNull { it.getNobaVec().distance(location) < 3 } ?: return

			flyingYangGlyphs.remove(armorStand)
			yangGlyphs[location] = Timestamp.now() + 5.seconds

			RenderUtils.drawTitle("Yang Glyph!", config.yangGlyphAlertColor.toNobaColor())
			SoundUtils.plingSound.play()
		} else {
			if(location in yangGlyphs) yangGlyphs.remove(location)
		}
	}

	private fun onEntityRender(event: EntityRenderEvents.Render) {
		if(!config.yangGlyphAlert) return
		if(!enabled) return

		val bossEntity = SlayerAPI.currentQuest?.entity ?: return

		val entity = event.entity as? ArmorStandEntity ?: return
		if(entity in flyingYangGlyphs) return

		val helmet = entity.getEquippedStack(EquipmentSlot.HEAD)
		if(helmet.name.string != "Beacon") return

		val entitiesNear = EntityUtils.getEntitiesNear<EndermanEntity>(entity.getNobaVec(), 3.0)
		if(bossEntity !in entitiesNear) return

		flyingYangGlyphs.add(entity)
	}

	private fun onWorldRender(context: WorldRenderContext) {
		if(!config.highlightYangGlyphs) return

		yangGlyphs.forEach { (location, timestamp) ->
			if(location.distanceToPlayer() > 20) return@forEach

			val seconds = timestamp.timeRemaining().toString(DurationUnit.SECONDS, 1)
			RenderUtils.renderOutlinedFilledBox(context, location, config.yangGlyphHighlightColor.toNobaColor(), throughBlocks = true)
			RenderUtils.renderText(location.center().raise(), "Yang Glyph", config.yangGlyphHighlightColor.toNobaColor(), yOffset = -10.0f, throughBlocks = true)
			RenderUtils.renderText(location.center().raise(), seconds, NobaColor.WHITE, throughBlocks = true)
		}
	}

	private fun reset() {
		yangGlyphs.clear()
		flyingYangGlyphs.clear()
	}
}