package me.nobaboy.nobaaddons.events.impl.client

import me.nobaboy.nobaaddons.events.Event
import me.nobaboy.nobaaddons.events.EventDispatcher
import net.minecraft.block.BlockState
import net.minecraft.client.world.ClientWorld
import net.minecraft.util.math.BlockPos

object WorldEvents {
	@JvmField val LOAD = EventDispatcher<Load>()

	@JvmField val BLOCK_UPDATE = EventDispatcher<BlockUpdate>()

	data class Load(val world: ClientWorld) : Event
	data class BlockUpdate(val blockPos: BlockPos, val newState: BlockState, val oldState: BlockState) : Event
}