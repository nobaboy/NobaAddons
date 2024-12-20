package me.nobaboy.nobaaddons.mixins.render;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI;
import me.nobaboy.nobaaddons.config.NobaConfigManager;
import me.nobaboy.nobaaddons.utils.items.ItemUtils;
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
			//? if >=1.21.2 {
			target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"
			//?} else {
			/*target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"
			*///?}
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

	@WrapOperation(
		method = "updateHeldItems",
		at = @At(
			value = "INVOKE",
			//? if >=1.21.4 {
			target = "Lnet/minecraft/client/render/item/HeldItemRenderer;shouldSkipHandAnimationOnSwap(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)Z"
			//?} else {
			/*target = "Lnet/minecraft/item/ItemStack;areEqual(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)Z"
			*///?}
		)
	)
	public boolean nobaaddons$cancelItemUpdateAnimation(/*? if >=1.21.4 {*/HeldItemRenderer renderer,/*?}*/ ItemStack left, ItemStack right, Operation<Boolean> original) {
		var config = NobaConfigManager.getConfig().getUiAndVisuals().getItemPosition();
		if(config.getCancelEquipAnimation()) {
			return true;
		}
		if(SkyBlockAPI.inSkyBlock() && config.getCancelItemUpdateAnimation()) {
			return ItemUtils.isEqual(left, right);
		}
		return original.call(/*? if >=1.21.4 {*/renderer,/*?}*/ left, right);
	}

	@Inject(method = "applyEatOrDrinkTransformation", at = @At(value = "INVOKE", target = "Ljava/lang/Math;pow(DD)D"), cancellable = true)
	public void nobaaddons$cancelDrinkAnimation(MatrixStack matrices, float tickDelta, Arm arm, ItemStack stack, PlayerEntity player, CallbackInfo ci) {
		if(NobaConfigManager.getConfig().getUiAndVisuals().getItemPosition().getCancelDrinkAnimation()) {
			ci.cancel();
		}
	}
}
