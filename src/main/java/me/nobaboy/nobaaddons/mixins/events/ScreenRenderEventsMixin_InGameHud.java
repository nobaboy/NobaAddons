package me.nobaboy.nobaaddons.mixins.events;

import me.nobaboy.nobaaddons.events.impl.render.ScreenRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
abstract class ScreenRenderEventsMixin_InGameHud {
	@Shadow @Final private MinecraftClient client;

	@Inject(
		method = "renderHotbarItem",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/DrawContext;drawStackOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;II)V"
		)
	)
	public void nobaaddons$onRenderHotbarItem(DrawContext context, int x, int y, RenderTickCounter tickCounter, PlayerEntity player, ItemStack itemStack, int seed, CallbackInfo ci) {
		var event = new ScreenRenderEvents.DrawItem(context, client.textRenderer, itemStack, x, y);
		ScreenRenderEvents.DRAW_ITEM.dispatch(event);
	}
}