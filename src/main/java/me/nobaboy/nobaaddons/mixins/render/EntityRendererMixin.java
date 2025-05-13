package me.nobaboy.nobaaddons.mixins.render;

import me.nobaboy.nobaaddons.events.impl.render.RenderStateUpdateEvent;
import me.nobaboy.nobaaddons.utils.render.state.EntityDataKey;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
abstract class EntityRendererMixin {
	@Inject(method = "getAndUpdateRenderState", at = @At("RETURN"))
	public void nobaaddons$captureEntity(Entity entity, float tickDelta, CallbackInfoReturnable<EntityRenderState> cir) {
		var state = cir.getReturnValue();
		EntityDataKey.ENTITY.put(state, entity);
		RenderStateUpdateEvent.EVENT.dispatch(new RenderStateUpdateEvent(entity, state));
	}
}
