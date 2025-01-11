package me.nobaboy.nobaaddons.mixins.events;

import me.nobaboy.nobaaddons.events.ChatMessageEvents;
import net.minecraft.client.network.message.MessageHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// priority = 900 is to ensure that this event invoker is applied before fabric api's chat events,
// so we can reliably get the unmodified (and un-canceled) message
@Mixin(value = MessageHandler.class, priority = 900)
abstract class ChatMessageEventsMixin_MessageHandler {
	@Inject(method = "onGameMessage", at = @At("HEAD"))
	public void nobaaddons$onChatMessage(Text message, boolean overlay, CallbackInfo ci) {
		if(!overlay) ChatMessageEvents.CHAT.invoke(new ChatMessageEvents.Chat(message));
	}
}
