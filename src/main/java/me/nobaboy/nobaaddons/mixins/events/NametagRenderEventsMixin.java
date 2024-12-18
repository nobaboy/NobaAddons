package me.nobaboy.nobaaddons.mixins.events;

//? if >=1.21.2 {
import me.nobaboy.nobaaddons.ducks.EntityStateCaptureDuck;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.EntityAttachmentType;
//?}

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
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
public abstract class NametagRenderEventsMixin {
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

	// This fucking sucks
	//? if >=1.21.2 {
	@ModifyExpressionValue(
		method = "render",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/client/render/entity/state/EntityRenderState;displayName:Lnet/minecraft/text/Text;",
			ordinal = 0
		)
	)
	public Text nobaaddons$modifyNametagVisibility(Text original, @Local(argsOnly = true) EntityRenderState state) {
	//?} else {
	/*@WrapOperation(
		method = "render",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/render/entity/EntityRenderer;hasLabel(Lnet/minecraft/entity/Entity;)Z"
		)
	)
	public boolean nobaaddons$modifyNametagVisibility(EntityRenderer<?> instance, Entity entity, Operation<Boolean> original) {
	*///?}
		//? if >=1.21.2 {
		Entity entity = ((EntityStateCaptureDuck) state).nobaaddons$getEntity();
		if(entity == null) return original;
		//?} else {
		/*boolean vanilla = original.call(instance, entity);
		*///?}
		var event = new EntityNametagRenderEvents.Visibility(entity, /*? if >=1.21.2 {*/original != null/*?} else {*//*vanilla*//*?}*/);
		EntityNametagRenderEvents.VISIBILITY.invoke(event);
		//? if >=1.21.2 {
		if(event.getShouldRender()) {
			// the game will only set this if it thinks that there is a name tag it should be rendering, so we have
			// to convince it that there is one ourselves...
			float tickDelta = MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(true);
			state.nameLabelPos = entity.getAttachments().getPoint(EntityAttachmentType.NAME_TAG, 0, entity.getLerpedYaw(tickDelta));
			return original != null ? original : Text.of("<force render>");
		}
		return null;
		//?} else {
		/*return event.getShouldRender();
		*///?}
	}

	@SuppressWarnings("InvalidInjectorMethodSignature")
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
				matrices.translate(0.0F, 9.0F * 1.15F * 0.025F, 0.0F);
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
