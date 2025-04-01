package me.nobaboy.nobaaddons.mixins.render.tint;

//? if >=1.21.2 {
import me.nobaboy.nobaaddons.ducks.EntityStateCaptureDuck;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
//?} else {
/*import net.minecraft.entity.LivingEntity;
 *///?}

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.nobaboy.nobaaddons.utils.render.EntityOverlay;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.util.math.ColorHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LivingEntityRenderer.class)
abstract class LivingEntityRendererMixin {
	@ModifyExpressionValue(
		method = "getOverlay",
		//? if >=1.21.2 {
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;hurt:Z"
		)
		//?} else {
		/*at = {
			@At(value = "FIELD", target = "Lnet/minecraft/entity/LivingEntity;hurtTime:I"),
			@At(value = "FIELD", target = "Lnet/minecraft/entity/LivingEntity;deathTime:I")
		}
		*///?}
	)
	private static /*? if >=1.21.2 {*/boolean/*?} else {*//*int*//*?}*/ nobaaddons$suppressVanillaDamageOverlay(
		/*? if >=1.21.2 {*/
		boolean original,
		@Local(argsOnly = true) LivingEntityRenderState state
	) {
		var entity = ((EntityStateCaptureDuck) state).nobaaddons$getEntity();
		/*?} else {*/
		/*int original,
		@Local(argsOnly = true) LivingEntity entity
	) {
	*///?}
		return (entity == null || !EntityOverlay.INSTANCE.contains(entity)) && original/*? if <=1.21.1 {*/ /*> 0 ? original : 0*//*?}*/;
	}

	//? if >=1.21.2 {
	@ModifyReturnValue(method = "getMixColor", at = @At("RETURN"))
	private int nobaaddons$changeColor(
		int original,
		@Local(argsOnly = true) LivingEntityRenderState state
	) {
		var entity = ((EntityStateCaptureDuck) state).nobaaddons$getEntity();
		var overlay = entity != null ? EntityOverlay.INSTANCE.get(entity) : null;
		if(overlay != null) {
			return ColorHelper/*? if <1.21.2 {*//*.Argb*//*?}*/.fullAlpha(overlay.getRgb());
		}
		return original;
	}
	//?}

	@ModifyArg(
		method = "getOverlay",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/OverlayTexture;getU(F)I")
	)
	private static float nobaaddons$forceOverlay(
		float whiteOverlayProgress,
		//? if >=1.21.2 {
		@Local(argsOnly = true) LivingEntityRenderState state
	) {
		var entity = ((EntityStateCaptureDuck) state).nobaaddons$getEntity();
		/*?} else {*/
		/*@Local(argsOnly = true) LivingEntity entity
	) {
	*///?}
		return entity != null && EntityOverlay.INSTANCE.contains(entity) ? 1f : whiteOverlayProgress;
	}
}