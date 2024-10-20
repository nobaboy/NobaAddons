package me.nobaboy.nobaaddons.utils.items

import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.NbtComponent
import net.minecraft.item.ItemStack
import java.util.WeakHashMap

object ItemUtils {
	private val ITEM_CACHE = WeakHashMap<NbtComponent, SkyBlockItemData>(256)

	val ItemStack.isSkyBlockItem: Boolean
		get() = !isEmpty && getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).contains("id")

	fun ItemStack.skyblockItem(): SkyBlockItemData {
		require(isSkyBlockItem) { "Stack is not a valid SkyBlock item" }

		val component = getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT)
		return ITEM_CACHE.getOrPut(component) { SkyBlockItemData(component.copyNbt()) }
	}

	fun ItemStack.getSkullTexture(): String? {
		val component = this.get(DataComponentTypes.PROFILE) ?: return null
		return component.properties["textures"].firstOrNull()?.value
	}
}