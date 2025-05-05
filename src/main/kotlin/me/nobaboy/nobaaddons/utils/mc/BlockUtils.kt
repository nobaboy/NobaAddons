package me.nobaboy.nobaaddons.utils.mc

import me.nobaboy.nobaaddons.utils.NobaVec
import net.minecraft.block.Block
import net.minecraft.block.BlockState

object BlockUtils {
	fun NobaVec.getBlockStateAt(): BlockState = MCUtils.world!!.getBlockState(toBlockPos())

	fun NobaVec.getBlockAt(): Block = getBlockStateAt().block
}