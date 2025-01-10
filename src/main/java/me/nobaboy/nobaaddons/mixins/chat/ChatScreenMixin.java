package me.nobaboy.nobaaddons.mixins.chat;

import me.nobaboy.nobaaddons.features.chat.CopyChatFeature;
import net.minecraft.client.gui.screen.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatScreen.class)
abstract class ChatScreenMixin {
	@Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
	public void nobaaddons$copyChat(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
		if(CopyChatFeature.copy(button, mouseX, mouseY)) {
			cir.setReturnValue(true);
		}
	}
}
