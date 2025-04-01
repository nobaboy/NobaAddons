package me.nobaboy.nobaaddons.mixins.render.tint;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.nobaboy.nobaaddons.utils.render.EntityOverlay;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RenderLayer.Overlay.class)
abstract class RenderLayerOverlayMixin {
	@ModifyExpressionValue(
		method = {"method_23555", "method_23556"},
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;getOverlayTexture()Lnet/minecraft/client/render/OverlayTexture;")
	)
	private static OverlayTexture nobaaddons$replaceOverlayTexture(OverlayTexture original) {
		var overlay = EntityOverlay.INSTANCE.getOverlay();
		return overlay != null ? overlay : original;
	}
}