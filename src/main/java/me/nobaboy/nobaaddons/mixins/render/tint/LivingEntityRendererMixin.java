package me.nobaboy.nobaaddons.mixins.render.tint;

import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.nobaboy.nobaaddons.utils.render.EntityOverlay;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.util.Nullables;
import net.minecraft.util.math.ColorHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * This is taken and adapted from Firmament, which is licensed under the GPL-3.0.
 * <br>
 * <a href="https://github.com/nea89o/Firmament/blob/master/src/main/java/moe/nea/firmament/mixins/render/entitytints/ChangeColorOfLivingEntities.java">Original source</a>
 */
@Mixin(LivingEntityRenderer.class)
abstract class LivingEntityRendererMixin {
	@ModifyReturnValue(method = "getMixColor", at = @At("RETURN"))
	private int nobaaddons$changeColor(int original, @Local(argsOnly = true) LivingEntityRenderState state) {
		return Nullables.mapOrElse(EntityOverlay.getRgb(state), ColorHelper::fullAlpha, original);
	}

	@ModifyArg(
		method = "getOverlay",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/OverlayTexture;getU(F)I")
	)
	private static float nobaaddons$forceOverlay(float whiteOverlayProgress, @Local(argsOnly = true) LivingEntityRenderState state) {
		return EntityOverlay.contains(state) ? 1f : whiteOverlayProgress;
	}
}