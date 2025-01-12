package me.nobaboy.nobaaddons.mixins.events;

//? if >=1.21.2 {
import me.nobaboy.nobaaddons.ducks.EntityStateCaptureDuck;
import net.minecraft.client.render.entity.state.EntityRenderState;
//?}

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import me.nobaboy.nobaaddons.events.EntityNametagRenderEvents;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityRenderer.class)
abstract class NametagRenderEventsMixin_EntityRenderer {
	@Shadow
	protected abstract void renderLabelIfPresent(
		//? if >=1.21.2 {
		EntityRenderState state,
		//?} else {
		/*Entity entity,
		*///?}
		Text text,
		MatrixStack matrices,
		VertexConsumerProvider vertexConsumers,
		int light
		/*? if <1.21.2 {*//*, float tickDelta*//*?}*/
	);

	@ModifyReturnValue(method = "hasLabel", at = @At("RETURN"))
	public boolean nobaaddons$modifyNametagVisibility(boolean original, @Local(argsOnly = true) Entity entity) {
		var event = new EntityNametagRenderEvents.Visibility(entity, original);
		EntityNametagRenderEvents.VISIBILITY.invoke(event);
		return event.getShouldRender();
	}

	//? if >=1.21.2 {
	// For whatever reason, the MC Dev plugin complains about this, but only on 1.21.2+.
	// This is despite the fact that this is, in fact, a valid injector.
	@SuppressWarnings("InvalidInjectorMethodSignature")
	//?}
	@WrapOperation(
		method = "render",
		at = @At(
			value = "INVOKE",
			//? if >=1.21.2 {
			target = "Lnet/minecraft/client/render/entity/EntityRenderer;renderLabelIfPresent(Lnet/minecraft/client/render/entity/state/EntityRenderState;Lnet/minecraft/text/Text;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"
			//?} else {
			/*target = "Lnet/minecraft/client/render/entity/EntityRenderer;renderLabelIfPresent(Lnet/minecraft/entity/Entity;Lnet/minecraft/text/Text;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IF)V"
			*///?}
		)
	)
	public void nobaaddons$nametagRender(
		//? if >=1.21.2 {
		EntityRenderer<?, ?> instance,
		EntityRenderState state,
		//?} else {
		/*EntityRenderer<?> instance,
		Entity entity,
		*///?}
		Text text,
		MatrixStack matrices,
		VertexConsumerProvider vertexConsumers,
		int light,
		//? if <1.21.2 {
		/*float tickDelta,
		*///?}
		Operation<Void> original
	) {
		//? if >=1.21.2 {
		Entity entity = ((EntityStateCaptureDuck) state).nobaaddons$getEntity();
		if(entity == null) {
			original.call(instance, state, text, matrices, vertexConsumers, light);
			return;
		}
		//?}

		var event = new EntityNametagRenderEvents.Nametag(entity);
		EntityNametagRenderEvents.EVENT.invoke(event);
		matrices.push();
		for(int i = 0; i < event.getTags().size(); i++) {
			var ntext = event.getTags().get(i);
			// TODO this causes the scoreboard objective in the below name slot to render multiple times on players;
			//      we don't presently use this on players however, so this isn't an issue I'm too worried about right now.
			renderLabelIfPresent(
				/*? if >=1.21.2 {*/state/*?} else {*//*entity*//*?}*/,
				ntext,
				matrices,
				vertexConsumers,
				light
				/*? if <1.21.2 {*//*, tickDelta*//*?}*/
			);
			if(i < event.getTags().size() - 1 || event.getRenderEntityName()) {
				matrices.translate(0f, 9f * 1.15f * 0.025f, 0f);
			}
		}
		if(event.getRenderEntityName()) {
			original.call(
				instance,
				/*? if >=1.21.2 {*/state/*?} else {*//*entity*//*?}*/,
				text == null ? entity.getDisplayName() : text,
				matrices,
				vertexConsumers,
				light
				/*? if <1.21.2 {*//*, tickDelta*//*?}*/
			);
		}
		matrices.pop();
	}
}
