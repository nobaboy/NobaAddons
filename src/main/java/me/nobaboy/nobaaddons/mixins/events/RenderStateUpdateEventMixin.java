package me.nobaboy.nobaaddons.mixins.events;

import me.nobaboy.nobaaddons.events.impl.render.RenderStateUpdateEvent;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
abstract class RenderStateUpdateEventMixin {
	@Inject(method = "getAndUpdateRenderState", at = @At("RETURN"))
	public void nobaaddons$captureEntity(Entity entity, float tickDelta, CallbackInfoReturnable<EntityRenderState> cir) {
		var state = cir.getReturnValue();
		RenderStateUpdateEvent.EVENT.dispatch(new RenderStateUpdateEvent(entity, state));
	}
}
