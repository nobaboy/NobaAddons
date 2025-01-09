package me.nobaboy.nobaaddons.mixins.render;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.nobaboy.nobaaddons.utils.render.EntityOverlay;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

// FIXME This causes an issue with versions under 1.21.3 related to entity state since it never existed then
@Mixin(LivingEntityRenderer.class)
abstract class LivingEntityRendererMixin {
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
}
