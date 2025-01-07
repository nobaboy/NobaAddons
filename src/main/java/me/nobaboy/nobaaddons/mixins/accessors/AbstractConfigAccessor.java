package me.nobaboy.nobaaddons.mixins.accessors;

import dev.celestialfault.celestialconfig.AbstractConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.nio.file.Path;

@Mixin(AbstractConfig.class)
public interface AbstractConfigAccessor {
	@Invoker Path callGetPath();
}
