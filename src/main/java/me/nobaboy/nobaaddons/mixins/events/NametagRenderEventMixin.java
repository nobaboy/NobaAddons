package me.nobaboy.nobaaddons.mixins.events;

import me.nobaboy.nobaaddons.utils.MixinKeys;
import net.minecraft.client.render.entity.state.EntityRenderState;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.nobaboy.nobaaddons.events.impl.render.EntityNametagRenderEvents;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityRenderer.class)
abstract class NametagRenderEventMixin {
	@Shadow
	protected abstract void renderLabelIfPresent(
		EntityRenderState state,
		Text text,
		MatrixStack matrices,
		VertexConsumerProvider vertexConsumers,
		int light
	);

	@WrapOperation(
		method = "render",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/render/entity/EntityRenderer;renderLabelIfPresent(Lnet/minecraft/client/render/entity/state/EntityRenderState;Lnet/minecraft/text/Text;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"
		)
	)
	public void nobaaddons$nametagRender(
		EntityRenderer<?, ?> instance,
		EntityRenderState state,
		Text text,
		MatrixStack matrices,
		VertexConsumerProvider vertexConsumers,
		int light,
		Operation<Void> original
	) {
		boolean renderOriginal = MixinKeys.RENDER_ORIGINAL_ENTITY_NAME.get(state);
		var event = new EntityNametagRenderEvents.Nametag(state);
		EntityNametagRenderEvents.EVENT.dispatch(event);
		matrices.push();
		for(int i = 0; i < event.getTags().size(); i++) {
			var ntext = event.getTags().get(i);
			// TODO this causes the scoreboard objective in the below name slot to render multiple times on players;
			//      we don't presently use this on players however, so this isn't an issue I'm too worried about right now.
			renderLabelIfPresent(state, ntext, matrices, vertexConsumers, light);
			if(i < event.getTags().size() - 1 || renderOriginal) {
				matrices.translate(0f, 9f * 1.15f * 0.025f, 0f);
			}
		}
		if(renderOriginal && text != null) {
			original.call(instance, state, text, matrices, vertexConsumers, light);
		}
		matrices.pop();
	}
}
