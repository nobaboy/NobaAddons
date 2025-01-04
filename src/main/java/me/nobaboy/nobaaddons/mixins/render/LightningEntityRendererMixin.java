package me.nobaboy.nobaaddons.mixins.render;

import me.nobaboy.nobaaddons.config.NobaConfig;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.LightningEntityRenderer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightningEntityRenderer.class)
abstract class LightningEntityRendererMixin {
	@Inject(method = "drawBranch", at = @At("HEAD"), cancellable = true)
	private static void nobaaddons$hideLightningBolt(
		Matrix4f matrix,
		VertexConsumer buffer,
		float x1,
		float z1,
		int y,
		float x2,
		float z2,
		float red,
		float green,
		float blue,
		float offset2,
		float offset1,
		boolean shiftEast1,
		boolean shiftSouth1,
		boolean shiftEast2,
		boolean shiftSouth2,
		CallbackInfo ci
	) {
		if(NobaConfig.INSTANCE.getUiAndVisuals().getRenderingTweaks().getHideLightningBolt()) ci.cancel();
	}
}
