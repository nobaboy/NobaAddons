package me.nobaboy.nobaaddons.features.slayers.voidgloom

import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI.inIsland
import me.nobaboy.nobaaddons.api.skyblock.SlayerAPI
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.core.SkyBlockIsland
import me.nobaboy.nobaaddons.core.slayer.SlayerBoss
import me.nobaboy.nobaaddons.events.impl.client.EntityEvents
import me.nobaboy.nobaaddons.events.impl.client.TickEvents
import me.nobaboy.nobaaddons.events.impl.client.WorldEvents
import me.nobaboy.nobaaddons.events.impl.skyblock.SkyBlockEvents
import me.nobaboy.nobaaddons.events.impl.skyblock.SlayerEvents
import me.nobaboy.nobaaddons.repo.Repo
import me.nobaboy.nobaaddons.utils.EntityUtils
import me.nobaboy.nobaaddons.utils.LocationUtils.distanceToPlayer
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.NobaVec
import me.nobaboy.nobaaddons.utils.Scheduler
import me.nobaboy.nobaaddons.utils.Timestamp
import me.nobaboy.nobaaddons.utils.getNobaVec
import me.nobaboy.nobaaddons.utils.items.ItemUtils.getSkullTexture
import me.nobaboy.nobaaddons.utils.render.EntityOverlay.highlight
import me.nobaboy.nobaaddons.utils.render.RenderUtils
import me.nobaboy.nobaaddons.utils.sound.SoundUtils
import me.nobaboy.nobaaddons.utils.toNobaVec
import me.nobaboy.nobaaddons.utils.tr
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.minecraft.block.Blocks
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.decoration.ArmorStandEntity
import net.minecraft.entity.mob.EndermanEntity
import net.minecraft.item.Items
import java.awt.Color
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

object VoidgloomSeraphFeatures {
	private val config get() = NobaConfig.slayers.voidgloom
	private val enabled: Boolean
		get() = SkyBlockIsland.THE_END.inIsland() && SlayerAPI.currentQuest?.boss == SlayerBoss.VOIDGLOOM

	private val NUKEKUBI_FIXATION_TEXTURE by Repo.skull(
		"nukekubi_fixation",
		"eyJ0aW1lc3RhbXAiOjE1MzQ5NjM0MzU5NjIsInByb2ZpbGVJZCI6ImQzNGFhMmI4MzFkYTRkMjY5NjU1ZTMzYzE0M2YwOTZjIiwicHJvZmlsZU5hbWUiOiJFbmRlckRyYWdvbiIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWIwNzU5NGUyZGYyNzM5MjFhNzdjMTAxZDBiZmRmYTExMTVhYmVkNWI5YjIwMjllYjQ5NmNlYmE5YmRiYjRiMyJ9fX0="
	)

	private var brokenHeartRadiation: BrokenHeartRadiation? = null

	private val yangGlyphs = mutableMapOf<NobaVec, Timestamp>()
	private val flyingYangGlyphs = mutableSetOf<ArmorStandEntity>()
	private val inBeaconPhase: Boolean get() = yangGlyphs.isNotEmpty() || flyingYangGlyphs.isNotEmpty()

	private val nukekubiFixations = mutableSetOf<ArmorStandEntity>()

	fun init() {
		SkyBlockEvents.ISLAND_CHANGE.register { reset() }
		SlayerEvents.QUEST_CLEAR.register { reset() }
		TickEvents.TICK.register { onTick() }
		EntityEvents.SPAWN.register(this::onEntitySpawn)
		EntityEvents.VEHICLE_CHANGE.register(this::onEntityVehicleChange)
		WorldEvents.BLOCK_UPDATE.register(this::onWorldBlockUpdate)
		WorldRenderEvents.AFTER_TRANSLUCENT.register(this::onWorldRender)
	}

	private fun onTick() {
		if(!config.highlightPhases || !enabled) return

		SlayerAPI.currentQuest?.takeIf { it.spawned && it.entity != null }?.let {
			val color = getHighlightColor(it.entity, it.armorStand)
			it.entity?.highlight(color)
		}
	}

	private fun onEntitySpawn(event: EntityEvents.Spawn) {
		if(!enabled) return

		val currentQuest = SlayerAPI.currentQuest ?: return
		if(!currentQuest.spawned) return

		val entity = currentQuest.entity ?: return
		val armorStand = event.entity as? ArmorStandEntity ?: return

		Scheduler.schedule(2) {
			val nearbyEntities = EntityUtils.getEntitiesNear<EndermanEntity>(armorStand.getNobaVec(), 3.0)
			if(entity !in nearbyEntities) return@schedule

			val helmet = armorStand.getEquippedStack(EquipmentSlot.HEAD)

			when(helmet.item) {
				Items.BEACON -> if(armorStand !in flyingYangGlyphs) flyingYangGlyphs.add(armorStand)
				Items.PLAYER_HEAD -> if(armorStand !in nukekubiFixations && helmet.getSkullTexture() == NUKEKUBI_FIXATION_TEXTURE) {
					nukekubiFixations.add(armorStand)
				}
			}
		}
	}

	private fun onEntityVehicleChange(event: EntityEvents.VehicleChange) {
		if(!config.brokenHeartRadiationTimer || !enabled) return

		val currentQuest = SlayerAPI.currentQuest ?: return
		if(!currentQuest.spawned) return

		val entity = currentQuest.entity ?: return
		if(event.entity != entity) return

		if(brokenHeartRadiation == null) brokenHeartRadiation = BrokenHeartRadiation(entity)
	}

	private fun onWorldBlockUpdate(event: WorldEvents.BlockUpdate) {
		if(!enabled) return

		val location = event.blockPos.toNobaVec()

		when(event.newState.block) {
			Blocks.BEACON -> {
				if(SlayerAPI.currentQuest?.spawned == false) return

				val armorStand = flyingYangGlyphs.firstOrNull { it.getNobaVec().distance(location) < 3 } ?: return
				flyingYangGlyphs.remove(armorStand)
				yangGlyphs[location] = Timestamp.now() + 5.seconds

				if(config.yangGlyphAlert) {
					RenderUtils.drawTitle(tr("nobaaddons.slayers.yangGlyph.placed", "Yang Glyph!"), config.yangGlyphAlertColor, duration = 1.5.seconds)
					SoundUtils.plingSound.play()
				}
			}
			else -> yangGlyphs.remove(location)
		}
	}

	private fun onWorldRender(context: WorldRenderContext) {
		if(!enabled) return

		val currentQuest = SlayerAPI.currentQuest ?: return
		if(!currentQuest.spawned || currentQuest.entity == null) return

		if(config.brokenHeartRadiationTimer) brokenHeartRadiation?.takeIf { it.isValid }?.let {
			RenderUtils.renderText(context, it.entity.getNobaVec().raise(1.5), it.remainingTime, color = NobaColor.GOLD, throughBlocks = true)
		} ?: run {
			brokenHeartRadiation = null
		}

		if(config.highlightYangGlyphs) yangGlyphs.forEach { (location, timestamp) ->
			if(location.distanceToPlayer() > 24) return@forEach

			val adjustedLocation = location.center().raise()

			val seconds = timestamp.timeRemaining().toString(DurationUnit.SECONDS, 1)
			RenderUtils.renderOutlinedFilledBox(context, location, config.yangGlyphHighlightColor, throughBlocks = true)
			RenderUtils.renderText(
				context,
				adjustedLocation,
				tr("nobaaddons.slayers.yangGlyph.name", "Yang Glyph"),
				color = config.yangGlyphHighlightColor,
				yOffset = -10f,
				throughBlocks = true
			)
			RenderUtils.renderText(context, adjustedLocation, seconds, NobaColor.WHITE, throughBlocks = true)
		}

		if(config.highlightNukekubiFixations) {
			nukekubiFixations.removeIf { !it.isAlive }
			nukekubiFixations.forEach { armorStand ->
				val location = armorStand.pos.toNobaVec()
				if(location.distanceToPlayer() > 24) return@forEach

				RenderUtils.renderOutline(context, location.add(x = -0.5, y = 0.65, z = -0.5), config.nukekubiFixationHighlightColor, throughBlocks = true)
			}
		}
	}

	private fun getHighlightColor(entity: LivingEntity?, armorStand: ArmorStandEntity?): Color {
		val armorStandName = armorStand?.name?.string.orEmpty()
		val isHoldingBeacon = (entity as? EndermanEntity)?.carriedBlock?.block == Blocks.BEACON

		return when {
			armorStandName.contains("Hits") && !inBeaconPhase -> config.hitsPhaseColor
			isHoldingBeacon || inBeaconPhase -> config.beaconPhaseColor
			else -> config.damagePhaseColor
		}
	}

	private fun reset() {
		brokenHeartRadiation = null
		yangGlyphs.clear()
		flyingYangGlyphs.clear()
		nukekubiFixations.clear()
	}

	data class BrokenHeartRadiation(
		val entity: LivingEntity,
		val timestamp: Timestamp = Timestamp.now() + 8.seconds
	) {
		val isValid: Boolean
			get() = timestamp.timeRemaining() > 0.seconds && (entity.vehicle != null || timestamp.timeRemaining() > 5.seconds)

		val remainingTime: String get() = timestamp.timeRemaining().toString(DurationUnit.SECONDS, 1)
	}
}