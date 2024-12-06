package me.nobaboy.nobaaddons.mixins;

import me.nobaboy.nobaaddons.screens.keybinds.KeyBindsManager;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardMixin {
	@Shadow
	@Final
	private MinecraftClient client;

	@Inject(method = "onKey", at = @At("TAIL"))
	public void nobaaddons$onKeyPress(long window, int keyCode, int scancode, int action, int modifiers, CallbackInfo ci) {
		if(window != client.getWindow().getHandle()) return;
		if(client.currentScreen != null) return;

		KeyBindsManager.onPress(keyCode);
	}
}
