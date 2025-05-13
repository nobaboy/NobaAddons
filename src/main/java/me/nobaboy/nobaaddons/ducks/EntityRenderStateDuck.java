package me.nobaboy.nobaaddons.ducks;

import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;

public interface EntityRenderStateDuck extends StateDataHolder {
	@Deprecated
	@Nullable Entity nobaaddons$getEntity();
}
