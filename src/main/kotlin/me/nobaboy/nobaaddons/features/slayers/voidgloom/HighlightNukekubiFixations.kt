package me.nobaboy.nobaaddons.features.slayers.voidgloom

import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI.inIsland
import me.nobaboy.nobaaddons.api.skyblock.SlayerAPI
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.core.SkyBlockIsland
import me.nobaboy.nobaaddons.core.slayer.SlayerBoss
import me.nobaboy.nobaaddons.events.EntityRenderEvents
import me.nobaboy.nobaaddons.events.skyblock.SkyBlockEvents
import me.nobaboy.nobaaddons.repo.Repo.skullFromRepo
import me.nobaboy.nobaaddons.utils.EntityUtils
import me.nobaboy.nobaaddons.utils.LocationUtils.distanceToPlayer
import me.nobaboy.nobaaddons.utils.getNobaVec
import me.nobaboy.nobaaddons.utils.items.ItemUtils.getSkullTexture
import me.nobaboy.nobaaddons.utils.render.RenderUtils
import me.nobaboy.nobaaddons.utils.toNobaVec
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.decoration.ArmorStandEntity
import net.minecraft.entity.mob.EndermanEntity

object HighlightNukekubiFixations {
	private val config get() = NobaConfig.INSTANCE.slayers.voidgloom
	private val enabled: Boolean
		get() = config.highlightNukekubiFixations && SkyBlockIsland.THE_END.inIsland() &&
			SlayerAPI.currentQuest?.let { it.boss == SlayerBoss.VOIDGLOOM && it.spawned } == true

	private val NUKEKUBI_FIXATION_TEXTURE by "eyJ0aW1lc3RhbXAiOjE1MzQ5NjM0MzU5NjIsInByb2ZpbGVJZCI6ImQzNGFhMmI4MzFkYTRkMjY5NjU1ZTMzYzE0M2YwOTZjIiwicHJvZmlsZU5hbWUiOiJFbmRlckRyYWdvbiIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWIwNzU5NGUyZGYyNzM5MjFhNzdjMTAxZDBiZmRmYTExMTVhYmVkNWI5YjIwMjllYjQ5NmNlYmE5YmRiYjRiMyJ9fX0=".skullFromRepo("nukekubi_fixatiion")

	private val nukekubiFixations = mutableListOf<ArmorStandEntity>()

	fun init() {
		SkyBlockEvents.ISLAND_CHANGE.register { nukekubiFixations.clear() }
		EntityRenderEvents.POST_RENDER.register(this::onEntityRender)
		WorldRenderEvents.AFTER_TRANSLUCENT.register(this::onWorldRender)
	}

	private fun onEntityRender(event: EntityRenderEvents.Render) {
		if(!enabled) return

		val bossEntity = SlayerAPI.currentQuest?.entity ?: return

		val entity = event.entity as? ArmorStandEntity ?: return
		if(entity in nukekubiFixations) return

		val helmet = entity.getEquippedStack(EquipmentSlot.HEAD)
		if(helmet.getSkullTexture() != NUKEKUBI_FIXATION_TEXTURE) return

		val entitiesNear = EntityUtils.getEntitiesNear<EndermanEntity>(entity.getNobaVec(), 5.0)
		if(bossEntity !in entitiesNear) return

		nukekubiFixations.add(entity)
	}

	private fun onWorldRender(context: WorldRenderContext) {
		nukekubiFixations.removeIf { !it.isAlive }
		nukekubiFixations.forEach { armorStand ->
			val location = armorStand.pos.toNobaVec()
			if(location.distanceToPlayer() > 20) return@forEach

			RenderUtils.renderOutline(context, location.add(x = -0.5, y = 0.65, z = -0.5), config.nukekubiFixationHighlightColor, throughBlocks = true)
		}
	}
}