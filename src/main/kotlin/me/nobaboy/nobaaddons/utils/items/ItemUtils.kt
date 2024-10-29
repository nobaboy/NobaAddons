package me.nobaboy.nobaaddons.utils.items

import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.NbtComponent
import net.minecraft.item.ItemStack
import java.lang.ref.WeakReference
import java.util.WeakHashMap

object ItemUtils {
	private val ITEM_CACHE = WeakHashMap<ItemStack, SkyBlockItemData>(256)

	val ItemStack.isSkyBlockItem: Boolean
		get() = !isEmpty && getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).contains("id")

	fun ItemStack.skyblockItem(): SkyBlockItemData {
		require(isSkyBlockItem) { "Stack is not a valid SkyBlock item" }
		return ITEM_CACHE.getOrPut(this) { SkyBlockItemData(WeakReference(this)) }
	}

	fun ItemStack.getSkullTexture(): String? {
		val component = this.get(DataComponentTypes.PROFILE) ?: return null
		return component.properties["textures"].firstOrNull()?.value
	}
}