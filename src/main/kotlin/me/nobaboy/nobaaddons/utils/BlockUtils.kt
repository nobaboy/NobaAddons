package me.nobaboy.nobaaddons.utils

import net.minecraft.block.Block
import net.minecraft.block.BlockState

object BlockUtils {
	fun NobaVec.getBlockAt(): Block = getBlockStateAt().block

	fun NobaVec.getBlockStateAt(): BlockState =
		MCUtils.world!!.getBlockState(toBlockPos())
}