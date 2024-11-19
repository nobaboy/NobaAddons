package me.nobaboy.nobaaddons.mixins.events;

import me.nobaboy.nobaaddons.events.ScreenRenderEvents;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public abstract class ScreenRenderEventsMixin extends Screen {
	protected ScreenRenderEventsMixin(Text title) {
		super(title);
	}

	@Inject(method = "drawSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawStackOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V"))
	public void nobaaddons$onDrawSlot(DrawContext context, Slot slot, CallbackInfo ci) {
		var event = new ScreenRenderEvents.DrawSlot(context, textRenderer, slot);
		ScreenRenderEvents.DRAW_SLOT.invoke(event);
	}
}