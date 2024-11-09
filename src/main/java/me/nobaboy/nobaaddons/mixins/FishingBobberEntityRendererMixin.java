package me.nobaboy.nobaaddons.mixins;

//? if >=1.21.2 {
import net.minecraft.client.render.entity.state.FishingBobberEntityState;
//?}
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import me.nobaboy.nobaaddons.config.NobaConfigManager;
import me.nobaboy.nobaaddons.utils.MCUtils;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.FishingBobberEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FishingBobberEntityRenderer.class)
public class FishingBobberEntityRendererMixin {
	private @Unique boolean shouldHide(FishingBobberEntity entity) {
		if(!NobaConfigManager.getConfig().getUiAndVisuals().getRenderingTweaks().getHideOtherPeopleFishing()) return false;
		if(entity == null) return false;
		return entity.getPlayerOwner() == MCUtils.INSTANCE.getPlayer();
	}
	//? if >=1.21.2 {
	@Inject(method = "render(Lnet/minecraft/client/render/entity/state/FishingBobberEntityState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"), cancellable = true)
	public void hideOtherPlayersBobbers(FishingBobberEntityState fishingBobberEntityState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci,
										@Share(namespace = "nobaaddons", value = "entity") LocalRef<Entity> entityRef) {
		var entity = entityRef.get();
		if(entity == null) return;
		if(shouldHide((FishingBobberEntity) entity)) ci.cancel();
	}
	//?} else {
	/*@Inject(method = "render(Lnet/minecraft/entity/projectile/FishingBobberEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"), cancellable = true)
	public void hideOtherPlayersBobbers(FishingBobberEntity entity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
		if(shouldHide(entity)) ci.cancel();
	}*/
	//?}
}