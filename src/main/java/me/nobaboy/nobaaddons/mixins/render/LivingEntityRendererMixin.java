package me.nobaboy.nobaaddons.mixins.render;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.nobaboy.nobaaddons.utils.render.EntityOverlay;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntityRenderer.class)
abstract class LivingEntityRendererMixin {
	//? if >=1.21.2 {
	@ModifyExpressionValue(
		method = "getOverlay",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;hurt:Z"
		)
	)
	private static boolean nobaaddons$forceOverlayWhereNecessary(boolean original) {
		return original || EntityOverlay.INSTANCE.getOverlay();
	}
	//?} else {
	/*@ModifyExpressionValue(
		method = "getOverlay",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/entity/LivingEntity;hurtTime:I"
		)
	)
	private static int nobaaddons$forceOverlayWhereNecessary(int original) {
		return EntityOverlay.INSTANCE.getOverlay() ? 1 : original;
	}
	*///?}
}
