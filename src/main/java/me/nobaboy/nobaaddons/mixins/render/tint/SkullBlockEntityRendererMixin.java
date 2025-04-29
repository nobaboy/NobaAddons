package me.nobaboy.nobaaddons.mixins.render.tint;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.nobaboy.nobaaddons.utils.render.EntityOverlay;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/*
 * This is taken and adapted from Firmament, which is licensed under the GPL-3.0.
 *
 * [Original source](https://github.com/nea89o/Firmament/blob/master/src/main/java/moe/nea/firmament/mixins/render/entitytints/UseOverlayableSkullBlockEntityRenderer.java)
 */
@Mixin(SkullBlockEntityRenderer.class)
abstract class SkullBlockEntityRendererMixin {
	@ModifyExpressionValue(
		method = "renderSkull",
		at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/OverlayTexture;DEFAULT_UV:I")
	)
	private static int nobaaddons$replaceUv(int original) {
		return EntityOverlay.getOverlay() != null ? OverlayTexture.packUv(15, 10) : original;
	}
}