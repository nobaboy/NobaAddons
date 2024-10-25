package me.nobaboy.nobaaddons.mixins;

import me.nobaboy.nobaaddons.config.NobaConfigManager;
import me.nobaboy.nobaaddons.utils.MCUtils;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.FishingBobberEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.projectile.FishingBobberEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FishingBobberEntityRenderer.class)
public class FishingBobberEntityRendererMixin {
	@Inject(method = "render(Lnet/minecraft/entity/projectile/FishingBobberEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"), cancellable = true)
	public void render(FishingBobberEntity fishingBobberEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
		if(NobaConfigManager.INSTANCE.get().getUiAndVisuals().getRenderingTweaks().getHideOtherPeopleFishing() &&
			fishingBobberEntity != null &&
			fishingBobberEntity.getPlayerOwner() != MCUtils.INSTANCE.getPlayer()) ci.cancel();
	}
}
