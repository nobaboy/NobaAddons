package me.nobaboy.nobaaddons.events.impl.client

import me.nobaboy.nobaaddons.events.Event
import me.nobaboy.nobaaddons.events.EventDispatcher
import net.minecraft.block.BlockState
import net.minecraft.client.world.ClientWorld
import net.minecraft.util.math.BlockPos

object WorldEvents {
	@JvmField val LOAD = Load.Companion
	@JvmField val BLOCK_UPDATE = BlockUpdate.Companion

	data class Load(val world: ClientWorld) : Event() {
		companion object : EventDispatcher<Load>()
	}

	data class BlockUpdate(val blockPos: BlockPos, val newState: BlockState, val oldState: BlockState) : Event() {
		companion object : EventDispatcher<BlockUpdate>()
	}
}