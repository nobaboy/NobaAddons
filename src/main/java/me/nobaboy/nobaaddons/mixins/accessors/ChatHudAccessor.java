package me.nobaboy.nobaaddons.mixins.accessors;

import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(ChatHud.class)
public interface ChatHudAccessor {
	@Accessor List<ChatHudLine.Visible> getVisibleMessages();
	@Invoker double callToChatLineX(double mouseX);
	@Invoker double callToChatLineY(double mouseY);
	@Invoker int callGetMessageIndex(double x, double y);
}
