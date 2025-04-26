package me.nobaboy.nobaaddons.mixins.events;

import me.nobaboy.nobaaddons.events.impl.render.ScreenRenderEvents;
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
abstract class ScreenRenderEventsMixin_HandledScreen extends Screen {
	protected ScreenRenderEventsMixin_HandledScreen(Text title) {
		super(title);
	}

	@Inject(
		method = "drawSlot",
		at = @At(
			value = "INVOKE",
			//? if >=1.21.2 {
			target = "Lnet/minecraft/client/gui/DrawContext;drawStackOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V"
			//?} else {
			/*target = "Lnet/minecraft/client/gui/DrawContext;drawItemInSlot(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V"
			*///?}
		)
	)
	public void nobaaddons$onDrawSlot(DrawContext context, Slot slot, CallbackInfo ci) {
		var event = new ScreenRenderEvents.DrawItem(context, textRenderer, slot.getStack(), slot.x, slot.y);
		ScreenRenderEvents.DRAW_ITEM.dispatch(event);
	}
}