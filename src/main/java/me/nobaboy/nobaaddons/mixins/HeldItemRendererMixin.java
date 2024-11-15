package me.nobaboy.nobaaddons.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.nobaboy.nobaaddons.config.NobaConfigManager;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {
	@ModifyExpressionValue(method = "updateHeldItems", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getAttackCooldownProgress(F)F"))
	public float nobaaddons$cancelAttackCooldown(float original) {
		if(NobaConfigManager.getConfig().getUiAndVisuals().getSwingAnimation().getSwingDuration() > 1) {
			return 1f;
		}
		return original;
	}

	@Inject(
		method = "renderFirstPersonItem",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"
		)
	)
	public void nobaaddons$modifyItemPosition(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand,
											  float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices,
											  VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
		var arm = hand == Hand.MAIN_HAND ? player.getMainArm() : player.getMainArm().getOpposite();
		var config = NobaConfigManager.getConfig().getUiAndVisuals().getItemPosition();
		float x = config.getX() / 100f;
		float y = config.getY() / 100f;
		float z = config.getZ() / 100f;
		float scale = config.getScale();

		// note that this means the X offset slider is technically inverted with a left hand main hand, but this
		// also ensures that the value remains consistent between the two options, which in my opinion is
		// slightly more preferable than having a fully consistent slider.
		if(arm == Arm.LEFT) {
			x = x > 0 ? -x : Math.abs(x);
		}

		matrices.translate(x, y, z);
		matrices.scale(scale, scale, scale);
	}

	@ModifyVariable(method = "applyEquipOffset", at = @At("HEAD"), argsOnly = true)
	public float nobaaddons$cancelEquipAnimation(float equipProgress) {
		if(NobaConfigManager.getConfig().getUiAndVisuals().getItemPosition().getCancelEquipAnimation()) {
			return 0f;
		}
		return equipProgress;
	}

	@Inject(method = "applyEatOrDrinkTransformation", at = @At(value = "INVOKE", target = "Ljava/lang/Math;pow(DD)D"), cancellable = true)
	public void nobaaddons$cancelDrinkAnimation(MatrixStack matrices, float tickDelta, Arm arm, ItemStack stack, PlayerEntity player, CallbackInfo ci) {
		if(NobaConfigManager.getConfig().getUiAndVisuals().getItemPosition().getCancelDrinkAnimation()) {
			ci.cancel();
		}
	}
}
