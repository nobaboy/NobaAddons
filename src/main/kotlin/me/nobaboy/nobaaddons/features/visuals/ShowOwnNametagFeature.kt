package me.nobaboy.nobaaddons.features.visuals

import me.nobaboy.nobaaddons.core.DebugFlag
import me.nobaboy.nobaaddons.events.impl.render.EntityNametagRenderEvents
import me.owdding.ktmodules.Module
import net.minecraft.client.network.ClientPlayerEntity

@Module
object ShowOwnNametagFeature {
	init {
		EntityNametagRenderEvents.VISIBILITY.register {
			// TODO make this into a proper feature? currently this only exists to verify the nametag render logic works properly
			if(!DebugFlag.SHOW_OWN_NAMETAG.enabled) return@register
			if(it.entity is ClientPlayerEntity) {
				it.renderOriginalNametag = true
			}
		}
	}
}