package me.nobaboy.nobaaddons.mixins.jgit;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.eclipse.jgit.lib.Constants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * This is taken from Skyblocker, which is licensed under the LGPL-3.0.
 * <br>
 * <a href="https://github.com/SkyblockerMod/Skyblocker/blob/3b52b7b130c1a95410ecef97b173fb0ac0c8e5ad/src/main/java/de/hysky/skyblocker/mixins/jgit/SystemReaderMixin.java">Original source</a>
 */
@Mixin(targets = "org.eclipse.jgit.util.SystemReader$Default", remap = false)
abstract class SystemReaderMixin {
	@ModifyReturnValue(method = "getenv", at = @At("RETURN"))
	private String nobaaddons$blockLoading(String original, String variable) {
		return variable.equals(Constants.GIT_CONFIG_NOSYSTEM_KEY) ? "FORCE-ENABLE" : original;
	}
}
