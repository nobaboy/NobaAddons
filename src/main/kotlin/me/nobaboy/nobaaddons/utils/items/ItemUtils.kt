package me.nobaboy.nobaaddons.utils.items

import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.LoreComponent
import net.minecraft.component.type.NbtComponent
import net.minecraft.item.ItemStack
import java.lang.ref.WeakReference
import java.util.WeakHashMap

object ItemUtils {
	private val ITEM_CACHE = WeakHashMap<ItemStack, SkyBlockItemData>(256)

	val ItemStack.nbtCompound get() = this.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT)
	val ItemStack.lore get() = this.getOrDefault(DataComponentTypes.LORE, LoreComponent.DEFAULT)

	val LoreComponent.stringLines get() = this.lines.map { it.string }

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

	fun ItemStack.getSkyBlockItem(): SkyBlockItemData? {
		if(!isSkyBlockItem) return null
		return skyblockItem()
	}

	fun ItemStack.getSkyBlockItemId(): String? = getSkyBlockItem()?.id

	@JvmStatic
	fun isEqual(first: ItemStack, second: ItemStack): Boolean {
		if(first.item !== second.item) return false
		return first.getSkyBlockItem() == second.getSkyBlockItem()
	}
}