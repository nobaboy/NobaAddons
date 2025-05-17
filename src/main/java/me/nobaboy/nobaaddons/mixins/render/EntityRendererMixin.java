package me.nobaboy.nobaaddons.mixins.render;

//? if >=1.21.2 {
import me.nobaboy.nobaaddons.ducks.EntityStateCaptureDuck;
import me.nobaboy.nobaaddons.events.impl.render.RenderStateUpdateEvent;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
abstract class EntityRendererMixin {
	@Shadow @Final
	private EntityRenderState state;

	@Inject(method = "getAndUpdateRenderState", at = @At("RETURN"))
	public void nobaaddons$captureEntity(Entity entity, float tickDelta, CallbackInfoReturnable<? extends EntityRenderState> cir) {
		var duck = (EntityStateCaptureDuck) state;
		duck.nobaaddons$setEntity(entity);
		RenderStateUpdateEvent.EVENT.dispatch(new RenderStateUpdateEvent(entity, state));
	}
}
//?}
