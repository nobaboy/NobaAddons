package me.nobaboy.nobaaddons.mixins.devenv;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.authlib.yggdrasil.YggdrasilServicesKeyInfo;
import com.moulberry.mixinconstraints.annotations.IfDevEnvironment;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfDevEnvironment
@Mixin(YggdrasilServicesKeyInfo.class)
public class YggdrasilServicesKeyInfoMixin {
	@WrapWithCondition(
		method = "validateProperty",
		at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V"),
		remap = false
	)
	public boolean nobaaddons$silenceSignatureStackTrace(Logger instance, String string, Object property, Object error) {
		return false;
	}
}
