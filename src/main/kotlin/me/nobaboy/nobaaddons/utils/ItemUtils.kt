package me.nobaboy.nobaaddons.utils

import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.NbtComponent
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound

object ItemUtils {
	fun ItemStack.getCustomData(): NbtCompound = getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt()

	fun ItemStack.getSkyblockID(): String = getCustomData().getString("id")
	fun NbtCompound.getSkyblockID(): String = getString("id")
}