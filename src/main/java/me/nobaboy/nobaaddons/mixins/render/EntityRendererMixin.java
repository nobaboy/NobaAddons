package me.nobaboy.nobaaddons.mixins.render;

//? if >=1.21.2 {
import me.nobaboy.nobaaddons.ducks.EntityStateCaptureDuck;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
abstract class EntityRendererMixin {
	@Inject(method = "updateRenderState", at = @At("TAIL"))
	public void nobaaddons$captureEntity(Entity entity, EntityRenderState state, float tickDelta, CallbackInfo ci) {
		var duck = (EntityStateCaptureDuck) state;
		duck.nobaaddons$setEntity(entity);
	}
}
//?}
