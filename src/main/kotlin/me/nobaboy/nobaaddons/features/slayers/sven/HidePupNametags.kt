package me.nobaboy.nobaaddons.features.slayers.sven

import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.events.impl.render.EntityNametagRenderEvents
import net.minecraft.entity.decoration.ArmorStandEntity

object HidePupNametags {
	private val config get() = NobaConfig.slayers.sven
	private val enabled: Boolean get() = config.hidePupNametags && SkyBlockAPI.inSkyBlock

	init {
		EntityNametagRenderEvents.VISIBILITY.register(this::onNametagRender)
	}

	private fun onNametagRender(event: EntityNametagRenderEvents.Visibility) {
		if(!enabled) return
		val entity = event.entity as? ArmorStandEntity ?: return
		if(entity.name.string.contains("Sven Pup")) event.shouldRender = false
	}
}