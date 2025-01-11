package me.nobaboy.nobaaddons.mixins.io;

import me.nobaboy.nobaaddons.features.general.DevFeatures;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.ScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HandledScreen.class)
abstract class HandledScreenMixin {
	@Inject(method = "keyPressed", at = @At("HEAD"))
	public void nobaaddons$copyItemData(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
		if(DevFeatures.shouldCopy(keyCode)) {
			DevFeatures.copyCurrentHoveredInventorySlot((HandledScreen<? extends ScreenHandler>)(Object)this);
		}
	}
}
