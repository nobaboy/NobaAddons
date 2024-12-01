package me.nobaboy.nobaaddons.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.nobaboy.nobaaddons.features.qol.MouseLock;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Mouse.class)
public class MouseMixin {
	@ModifyExpressionValue(method = "updateMouse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/SimpleOption;getValue()Ljava/lang/Object;", ordinal = 0))
	public Object nobaaddons$mouseLock(Object original) {
		if(MouseLock.isLocked()) return -1 / 3d;
		if(MouseLock.isReduced()) return ((double) original) / 6d;
		return original;
	}
}