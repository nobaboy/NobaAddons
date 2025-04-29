package me.nobaboy.nobaaddons.mixins.render.tint;

// TODO fix this for 1.21.1 (or just drop 1.21.1)
//? if >=1.21.2 {
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.nobaboy.nobaaddons.utils.render.EntityOverlay;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.equipment.EquipmentRenderer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * This is taken and adapted from Firmament, which is licensed under the GPL-3.0.
 * <br>
 * <a href="https://github.com/nea89o/Firmament/blob/master/src/main/java/moe/nea/firmament/mixins/render/entitytints/UseOverlayableEquipmentRenderer.java">Original source</a>
 */
@Mixin(EquipmentRenderer.class)
abstract class EquipmentRendererMixin {
	@WrapOperation(
		//? if >=1.21.4 {
		method = "render(Lnet/minecraft/client/render/entity/equipment/EquipmentModel$LayerType;Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/client/model/Model;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/util/Identifier;)V",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;getArmorCutoutNoCull(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/RenderLayer;")
		//?} else {
		/*method = "render(Lnet/minecraft/item/equipment/EquipmentModel$LayerType;Lnet/minecraft/util/Identifier;Lnet/minecraft/client/model/Model;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/util/Identifier;)V",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;getArmorCutoutNoCull(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/RenderLayer;")
		*///?}
	)
	public RenderLayer nobaaddons$replaceRenderLayer(Identifier texture, Operation<RenderLayer> original) {
		return EntityOverlay.getOverlay() != null ? RenderLayer.getEntityTranslucent(texture) : original.call(texture);
	}

	@ModifyExpressionValue(
		//? if >=1.21.4 {
		method = "render(Lnet/minecraft/client/render/entity/equipment/EquipmentModel$LayerType;Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/client/model/Model;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/util/Identifier;)V",
		at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/OverlayTexture;DEFAULT_UV:I")
		//?} else {
		/*method = "render(Lnet/minecraft/item/equipment/EquipmentModel$LayerType;Lnet/minecraft/util/Identifier;Lnet/minecraft/client/model/Model;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/util/Identifier;)V",
		at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/OverlayTexture;DEFAULT_UV:I")
		*///?}
	)
	public int nobaaddons$replaceUv(int original) {
		return EntityOverlay.getOverlay() != null ? OverlayTexture.packUv(15, 10) : original;
	}
}
//?}