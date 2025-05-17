package me.nobaboy.nobaaddons.mixins.duckimpl;

import me.nobaboy.nobaaddons.ducks.ItemSkyblockDataCache;
import me.nobaboy.nobaaddons.utils.items.SkyBlockItemData;
import me.nobaboy.nobaaddons.utils.properties.Holding;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@SuppressWarnings("unused")
@Mixin(ItemStack.class)
abstract class ItemSkyblockDataCacheImpl implements ItemSkyblockDataCache {
	@Unique
	private final Holding<SkyBlockItemData> nobaaddons$skyblockData = new Holding<>();

	@Override
	public @NotNull SkyBlockItemData nobaaddons$getSkyblockData() {
		return nobaaddons$skyblockData.getOrSet(this::nobaaddons$createSkyblockItemData);
	}
}
