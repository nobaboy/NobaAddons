package me.nobaboy.nobaaddons.utils.items

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import me.nobaboy.nobaaddons.config.NobaConfig
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

	val ItemStack.nbt: NbtComponent get() = this.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT)
	val ItemStack.lore: LoreComponent get() = this.getOrDefault(DataComponentTypes.LORE, LoreComponent.DEFAULT)

	val LoreComponent.stringLines get() = this.lines.map { it.string }

	@JvmStatic
	val ItemStack.isSkyBlockItem: Boolean
		get() = !isEmpty && getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).contains("id")

	val ItemStack.asSkyBlockItem: SkyBlockItemData? get() {
		if(!isSkyBlockItem) return null
		return ITEM_CACHE.getUnchecked(this)
	}

	val ItemStack.skyBlockId: String? get() = asSkyBlockItem?.id

	fun ItemStack.getSkullTexture(): String? {
		val component = this.get(DataComponentTypes.PROFILE) ?: return null
		return component.properties["textures"].firstOrNull()?.value
	}

	@JvmStatic
	fun isEqual(first: ItemStack, second: ItemStack): Boolean {
		if(first.item !== second.item) return false
		return first.asSkyBlockItem == second.asSkyBlockItem
	}

	@JvmStatic
	fun shouldArmorHaveEnchantGlint(item: ItemStack, original: Boolean): Boolean {
		val config = NobaConfig.INSTANCE.uiAndVisuals.renderingTweaks

		if(config.removeArmorGlints) return false
		if(!config.fixEnchantedArmorGlint) return original
		if(original) return true // don't remove enchant glints past this point, only add missing ones

		val data = item.asSkyBlockItem ?: return false
		return data.enchantments.isNotEmpty() || data.runes.isNotEmpty()
	}
}