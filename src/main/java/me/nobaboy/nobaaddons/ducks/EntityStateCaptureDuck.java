package me.nobaboy.nobaaddons.ducks;

import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;

//? if >=1.21.2 {
public interface EntityStateCaptureDuck {
	@Nullable Entity nobaaddons$getEntity();
	void nobaaddons$setEntity(@Nullable Entity entity);
}
//?}
