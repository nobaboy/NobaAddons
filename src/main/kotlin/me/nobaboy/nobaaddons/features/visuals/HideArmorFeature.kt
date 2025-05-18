package me.nobaboy.nobaaddons.features.visuals

//? if >=1.21.2 {
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.events.impl.render.RenderStateUpdateEvent
import me.nobaboy.nobaaddons.utils.MCUtils
import net.minecraft.client.render.entity.state.PlayerEntityRenderState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack

object HideArmorFeature {
	private val config by NobaConfig.uiAndVisuals::hideArmor

	init {
		RenderStateUpdateEvent.EVENT.register(this::updateRenderState)
	}

	private fun updateRenderState(event: RenderStateUpdateEvent) {
		if(!config.enabled) return

		val entity = event.entity
		val state = event.state
		if(state !is PlayerEntityRenderState || entity !is PlayerEntity) return
		if(entity != MCUtils.player && config.onlyClientPlayer) return

		state.equippedHeadStack = ItemStack.EMPTY
		state.equippedChestStack = ItemStack.EMPTY
		state.equippedLegsStack = ItemStack.EMPTY
		state.equippedFeetStack = ItemStack.EMPTY
	}
}
//?}