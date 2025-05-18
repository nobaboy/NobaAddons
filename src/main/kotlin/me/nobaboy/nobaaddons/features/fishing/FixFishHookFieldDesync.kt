package me.nobaboy.nobaaddons.features.fishing

import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.utils.EntityUtils
import me.nobaboy.nobaaddons.utils.ErrorManager
import me.nobaboy.nobaaddons.utils.MCUtils
import me.owdding.ktmodules.Module
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.entity.projectile.FishingBobberEntity

@Module
object FixFishHookFieldDesync {
	val enabled get() = NobaConfig.fishing.fixFishHookFieldDesync

	init {
		ClientTickEvents.START_CLIENT_TICK.register {
			ErrorManager.catching("Fix fishing bobber desync errored", this::onTick)
		}
	}

	private fun onTick() {
		if(!enabled) return
		val player = MCUtils.player ?: return
		val bobber = EntityUtils.getEntities<FishingBobberEntity>().firstOrNull { it.playerOwner == player }
		// using a mixin to FishingBobberEntity#setPlayerFishHook to more directly fix this issue is *technically*
		// possible, and may be better, but this also works, and takes a lot less effort. therefore, this is good enough.
		player.fishHook = bobber
	}
}