package me.nobaboy.nobaaddons.utils.items

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import me.nobaboy.nobaaddons.config.NobaConfigManager
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.LoreComponent
import net.minecraft.component.type.NbtComponent
import net.minecraft.item.ItemStack
import java.lang.ref.WeakReference

object ItemUtils {
	private val ITEM_CACHE: LoadingCache<ItemStack, SkyBlockItemData> = CacheBuilder.newBuilder()
		.weakKeys()
		.build(object : CacheLoader<ItemStack, SkyBlockItemData>() {
			override fun load(key: ItemStack) = SkyBlockItemData(WeakReference(key))
		})

	val ItemStack.nbtCompound get() = this.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT)
	val ItemStack.lore get() = this.getOrDefault(DataComponentTypes.LORE, LoreComponent.DEFAULT)

	val LoreComponent.stringLines get() = this.lines.map { it.string }

	val ItemStack.isSkyBlockItem: Boolean
		get() = !isEmpty && getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).contains("id")

	fun ItemStack.skyblockItem(): SkyBlockItemData {
		require(isSkyBlockItem) { "Stack is not a valid SkyBlock item" }
		return ITEM_CACHE.getUnchecked(this)
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

	@JvmStatic
	fun shouldArmorHaveEnchantGlint(item: ItemStack, original: Boolean): Boolean {
		var config = NobaConfigManager.config.uiAndVisuals.renderingTweaks

		if(config.removeArmorGlints) return false
		if(!config.fixEnchantedArmorGlint) return original
		if(original) return true // don't remove enchant glints past this point, only add missing ones

		var data = item.getSkyBlockItem() ?: return false
		return data.enchantments.isNotEmpty() || data.runes.isNotEmpty()
	}
}