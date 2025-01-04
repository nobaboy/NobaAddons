package me.nobaboy.nobaaddons.features.slayers.voidgloom

import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI.inIsland
import me.nobaboy.nobaaddons.api.skyblock.SlayerAPI
import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.core.SkyBlockIsland
import me.nobaboy.nobaaddons.core.slayer.SlayerBoss
import me.nobaboy.nobaaddons.events.EntityRenderEvents
import me.nobaboy.nobaaddons.events.PacketEvents
import me.nobaboy.nobaaddons.events.SecondPassedEvent
import me.nobaboy.nobaaddons.events.skyblock.SkyBlockEvents
import me.nobaboy.nobaaddons.repo.Repo.skullFromRepo
import me.nobaboy.nobaaddons.utils.EntityUtils
import me.nobaboy.nobaaddons.utils.LocationUtils.distanceToPlayer
import me.nobaboy.nobaaddons.utils.NobaColor.Companion.toNobaColor
import me.nobaboy.nobaaddons.utils.NobaVec
import me.nobaboy.nobaaddons.utils.Timestamp
import me.nobaboy.nobaaddons.utils.getNobaVec
import me.nobaboy.nobaaddons.utils.items.ItemUtils.getSkullTexture
import me.nobaboy.nobaaddons.utils.render.RenderUtils
import me.nobaboy.nobaaddons.utils.sound.SoundUtils
import me.nobaboy.nobaaddons.utils.toNobaVec
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.minecraft.block.Blocks
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.decoration.ArmorStandEntity
import net.minecraft.entity.mob.EndermanEntity
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket
import net.minecraft.network.packet.s2c.play.EntityPassengersSetS2CPacket
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

object VoidgloomFeatures {
	private val config get() = NobaConfigManager.config.slayers.voidgloom
	private val enabled: Boolean
		get() = SkyBlockIsland.THE_END.inIsland() &&
			SlayerAPI.currentQuest?.let { it.boss == SlayerBoss.VOIDGLOOM && it.spawned == true } == true

	private val NUKEKUBI_FIXATION_TEXTURE by "eyJ0aW1lc3RhbXAiOjE1MzQ5NjM0MzU5NjIsInByb2ZpbGVJZCI6ImQzNGFhMmI4MzFkYTRkMjY5NjU1ZTMzYzE0M2YwOTZjIiwicHJvZmlsZU5hbWUiOiJFbmRlckRyYWdvbiIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWIwNzU5NGUyZGYyNzM5MjFhNzdjMTAxZDBiZmRmYTExMTVhYmVkNWI5YjIwMjllYjQ5NmNlYmE5YmRiYjRiMyJ9fX0=".skullFromRepo("nukekubi_fixatiion")

	private val yangGlyphs = mutableMapOf<NobaVec, Timestamp>()
	private val flyingYangGlyphs = mutableListOf<ArmorStandEntity>()
	private val nukekubiFixations = mutableListOf<ArmorStandEntity>()
	private var brokenHeartRadiation: BrokenHeartRadiation? = null

	fun init() {
		SkyBlockEvents.ISLAND_CHANGE.register { reset() }
		SecondPassedEvent.EVENT.register { onSecondPassed() }
		PacketEvents.RECEIVE.register(this::onPacketReceive)
		EntityRenderEvents.POST_RENDER.register(this::onEntityRender)
		WorldRenderEvents.AFTER_TRANSLUCENT.register(this::onWorldRender)
	}

	private fun onSecondPassed() {
		nukekubiFixations.removeIf { !it.isAlive }
		flyingYangGlyphs.removeIf { !it.isAlive }
		yangGlyphs.entries.removeIf { it.value.elapsedSince() > 7.seconds }
	}

	private fun onPacketReceive(event: PacketEvents.Receive) {
		if(!enabled) return

		when(val packet = event.packet) {
			is BlockUpdateS2CPacket -> onBlockUpdate(packet)
			is EntityPassengersSetS2CPacket -> onEntityPassengersSet(packet)
		}
	}

	private fun onBlockUpdate(packet: BlockUpdateS2CPacket) {
		if(!config.yangGlyphAlert) return

		val location = packet.pos.toNobaVec()
		if(packet.state.block == Blocks.BEACON) {
			val armorStand = flyingYangGlyphs.firstOrNull { it.getNobaVec().distance(location) < 3 } ?: return

			flyingYangGlyphs.remove(armorStand)
			yangGlyphs[location] = Timestamp.now()

			RenderUtils.drawTitle("Yang Glyph!", config.alertColor.toNobaColor())
			SoundUtils.plingSound.play()
		} else {
			if(location in yangGlyphs) yangGlyphs.remove(location)
		}
	}

	private fun onEntityPassengersSet(packet: EntityPassengersSetS2CPacket) {
		if (!config.brokenHeartRadiationTimer) return

		val bossEntity = SlayerAPI.currentQuest?.entity ?: return
		if (bossEntity.id in packet.passengerIds && brokenHeartRadiation == null) {
			brokenHeartRadiation = BrokenHeartRadiation(bossEntity, Timestamp.now() + 8.seconds)
		}
	}

	private fun onEntityRender(event: EntityRenderEvents.Render) {
		if(!enabled) return
		if(!config.yangGlyphAlert && !config.highlightNukekubi) return

		val bossEntity = SlayerAPI.currentQuest?.entity ?: return
		val entity = event.entity

		if(entity is ArmorStandEntity) {
			val helmet = entity.getEquippedStack(EquipmentSlot.HEAD)

			if(config.yangGlyphAlert && entity !in flyingYangGlyphs && helmet.name.string == "Beacon") {
				val entitiesNear = EntityUtils.getEntitiesNear<EndermanEntity>(entity.getNobaVec(), 3.0)
				if(bossEntity in entitiesNear) flyingYangGlyphs.add(entity)
			}

			if(config.highlightNukekubi && entity !in nukekubiFixations && helmet.getSkullTexture() == NUKEKUBI_FIXATION_TEXTURE) {
				val entitiesNear = EntityUtils.getEntitiesNear<EndermanEntity>(entity.getNobaVec(), 5.0)
				if(bossEntity in entitiesNear) nukekubiFixations.add(entity)
			}
		}
	}

	private fun onWorldRender(context: WorldRenderContext) {
		if(!enabled) return

		if(config.highlightYangGlyph) highlightYangGlyphs(context)
		if(config.highlightNukekubi) highlightNukekubiFixations(context)
		if(config.brokenHeartRadiationTimer) renderBrokenHeartRadiationTimer()
	}

	private fun highlightYangGlyphs(context: WorldRenderContext) {
		yangGlyphs.forEach { (location, _) ->
			if(location.distanceToPlayer() > 20) return@forEach
			RenderUtils.renderOutlinedFilledBox(context, location, config.highlightColor.toNobaColor(), throughBlocks = true)
			RenderUtils.renderText(location.center().raise(), "Yang Glyph", yOffset = -10.0f, throughBlocks = true)
		}
	}

	private fun highlightNukekubiFixations(context: WorldRenderContext) {
		nukekubiFixations.removeIf { !it.isAlive }
		nukekubiFixations.forEach { armorStand ->
			val location = armorStand.pos.toNobaVec()
			if(location.distanceToPlayer() > 20) return@forEach
			RenderUtils.renderOutline(context, location.add(x = -0.5, y = 0.65, z = -0.5), config.highlightColor.toNobaColor(), throughBlocks = true)
		}
	}

	private fun renderBrokenHeartRadiationTimer() {
		brokenHeartRadiation?.let {
			if(!it.isValid) {
				brokenHeartRadiation = null
				return
			}

			RenderUtils.renderText(it.bossEntity.getNobaVec().raise(1.5), it.remainingTime)
		}
	}

	private fun reset() {
		yangGlyphs.clear()
		flyingYangGlyphs.clear()
		nukekubiFixations.clear()
		brokenHeartRadiation = null
	}

	data class BrokenHeartRadiation(val bossEntity: Entity, val timestamp: Timestamp) {
		val isValid: Boolean
			get() = timestamp.timeRemaining() > 0.seconds && (bossEntity.vehicle != null || timestamp.timeRemaining() >= 5.seconds)

		val remainingTime: String get() = timestamp.timeRemaining().toString(DurationUnit.SECONDS, 1)
	}
}