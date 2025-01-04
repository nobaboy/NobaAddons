package me.nobaboy.nobaaddons.mixins.io;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.nobaboy.nobaaddons.config.NobaConfig;
import me.nobaboy.nobaaddons.features.keybinds.KeyBindsManager;
import me.nobaboy.nobaaddons.features.qol.MouseLock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
abstract class MouseMixin {
	@Shadow @Final private MinecraftClient client;

	@Inject(method = "onMouseButton", at = @At("TAIL"))
	public void nobaaddons$onMouseButton(long window, int button, int action, int modifiers, CallbackInfo ci) {
		if(window != client.getWindow().getHandle()) return;
		if(client.currentScreen != null) return;

		KeyBindsManager.onPress(button);
	}

	@ModifyExpressionValue(method = "updateMouse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/SimpleOption;getValue()Ljava/lang/Object;", ordinal = 0))
	public Object nobaaddons$mouseLock(Object original) {
		if(MouseLock.isLocked()) return -1 / 3d;
		if(MouseLock.isReduced()) return ((double) original) / NobaConfig.INSTANCE.getQol().getGarden().getReductionMultiplier();

		return original;
	}
}