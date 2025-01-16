package me.nobaboy.nobaaddons.mixins.events;

import me.nobaboy.nobaaddons.events.impl.chat.SendMessageEvents;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayNetworkHandler.class)
abstract class SendMessageEventsMixin {
	@Inject(method = "sendChatCommand", at = @At("HEAD"), cancellable = true)
	public void nobaaddons$onChatCommand(String command, CallbackInfo ci) {
		if(SendMessageEvents.SEND_COMMAND.invoke(new SendMessageEvents.SendCommand(command))) {
			ci.cancel();
		}
	}

	@Inject(method = "sendCommand", at = @At("HEAD"), cancellable = true)
	public void nobaaddons$onCommand(String command, CallbackInfoReturnable<Boolean> cir) {
		if(SendMessageEvents.SEND_COMMAND.invoke(new SendMessageEvents.SendCommand(command))) {
			cir.setReturnValue(false);
		}
	}
}
