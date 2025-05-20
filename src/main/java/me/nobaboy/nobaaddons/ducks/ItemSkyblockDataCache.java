package me.nobaboy.nobaaddons.ducks;

import me.nobaboy.nobaaddons.utils.items.SkyBlockItemData;
import net.minecraft.component.ComponentHolder;
import net.minecraft.component.DataComponentTypes;
import org.jetbrains.annotations.NotNull;

public interface ItemSkyblockDataCache extends ComponentHolder {
	@SuppressWarnings("unused") // loud incorrect buzzer
	@NotNull SkyBlockItemData nobaaddons$getSkyblockData();

	default SkyBlockItemData nobaaddons$createSkyblockItemData() {
		return new SkyBlockItemData(() -> get(DataComponentTypes.CUSTOM_DATA), () -> get(DataComponentTypes.LORE), this::hashCode);
	}
}
