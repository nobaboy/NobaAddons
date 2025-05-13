package me.nobaboy.nobaaddons.ducks;

import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;

public interface EntityRenderStateDuck {
	@Nullable Entity nobaaddons$getEntity();
	void nobaaddons$setEntity(@Nullable Entity entity);
}
