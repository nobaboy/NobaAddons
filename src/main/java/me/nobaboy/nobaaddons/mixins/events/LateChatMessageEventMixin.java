package me.nobaboy.nobaaddons.mixins.events;

import me.nobaboy.nobaaddons.events.LateChatMessageEvent;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = ChatHud.class, priority = 10000)
public class LateChatMessageEventMixin {
	@ModifyVariable(
		method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V",
		at = @At("HEAD"),
		argsOnly = true
	)
	public Text nobaaddons$lateModifyMessage(Text original) {
		var event = new LateChatMessageEvent(original);
		LateChatMessageEvent.EVENT.invoke(event);
		return event.getMessage();
	}
}
