package me.nobaboy.nobaaddons.mixins.events;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.nobaboy.nobaaddons.events.impl.chat.ChatMessageEvents;
import me.nobaboy.nobaaddons.utils.chat.Message;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin(value = ChatHud.class, priority = 10000)
abstract class ChatMessageEventsMixin_ChatHud {
	private final @Unique ThreadLocal<@Nullable Message> MESSAGE = new ThreadLocal<>();

	@WrapOperation(
		method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V",
		at = @At(
			value = "NEW",
			target = "(ILnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)Lnet/minecraft/client/gui/hud/ChatHudLine;"
		)
	)
	public ChatHudLine nobaaddons$getActualMessage(int creationTick, Text text, MessageSignatureData messageSignatureData, MessageIndicator messageIndicator,
												   Operation<ChatHudLine> original) {
		var line = original.call(creationTick, text, messageSignatureData, messageIndicator);
		Message message = new Message(text, line, new ArrayList<>());
		MESSAGE.set(message);
		return line;
	}

	@WrapOperation(
		method = "addVisibleMessage",
		at = @At(
			value = "NEW",
			target = "(ILnet/minecraft/text/OrderedText;Lnet/minecraft/client/gui/hud/MessageIndicator;Z)Lnet/minecraft/client/gui/hud/ChatHudLine$Visible;"
		)
	)
	public ChatHudLine.Visible nobaaddons$associateVisibleLine(int tick, OrderedText text, MessageIndicator indicator,
															   boolean endOfEntry, Operation<ChatHudLine.Visible> original) {
		var visible = original.call(tick, text, indicator, endOfEntry);
		var message = MESSAGE.get();
		if(message != null) {
			message.getVisible().add(visible);
		}
		return visible;
	}

	@Inject(
		method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V",
		at = @At("RETURN")
	)
	public void nobaaddons$clearLocal(Text text, MessageSignatureData signatureData, MessageIndicator indicator, CallbackInfo ci) {
		var message = MESSAGE.get();
		if(message != null) {
			ChatMessageEvents.ADDED.dispatch(new ChatMessageEvents.Added(message));
		}
		MESSAGE.remove();
	}
}
