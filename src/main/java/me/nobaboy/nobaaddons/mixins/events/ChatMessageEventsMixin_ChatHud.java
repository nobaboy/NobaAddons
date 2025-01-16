package me.nobaboy.nobaaddons.mixins.events;

import me.nobaboy.nobaaddons.events.impl.chat.ChatMessageEvents;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = ChatHud.class, priority = 10000)
abstract class ChatMessageEventsMixin_ChatHud {
	@ModifyVariable(
		method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V",
		at = @At("HEAD"),
		argsOnly = true
	)
	public Text nobaaddons$lateModifyMessage(Text original) {
		return ChatMessageEvents.LATE_MODIFY.invoke(new ChatMessageEvents.Modify(original));
	}
}
