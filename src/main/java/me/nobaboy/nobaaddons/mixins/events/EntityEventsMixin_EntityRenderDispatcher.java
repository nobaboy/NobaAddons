package me.nobaboy.nobaaddons.mixins.events;

//? if >=1.21.5 {
/*import net.minecraft.client.MinecraftClient;
*///?}

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import me.nobaboy.nobaaddons.events.impl.client.EntityEvents;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
abstract class EntityEventsMixin_EntityRenderDispatcher {
	@Inject(
		//? if >=1.21.5 {
		/*method = "render(Lnet/minecraft/client/render/entity/state/EntityRenderState;DDDLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/EntityRenderer;)V",
		at = @At("HEAD"),
		*///?} else {
		method = "render(Lnet/minecraft/entity/Entity;DDDFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/EntityRenderer;)V",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/render/entity/EntityRenderer;getPositionOffset(Lnet/minecraft/client/render/entity/state/EntityRenderState;)Lnet/minecraft/util/math/Vec3d;"
		),
		//?}
		cancellable = true
	)
	public void nobaaddons$cancelEntityRender(
		//? if >=1.21.5 {
		/*EntityRenderState state,
		*///?} else {
		Entity entity,
		//?}
		double x, double y, double z,
		//? if <1.21.5 {
		float tickDelta,
		//?}
		MatrixStack matrices, VertexConsumerProvider vertexConsumers,
		int light,
		EntityRenderer<?, ?> renderer,
		CallbackInfo ci /*? if <1.21.5 {*/,
		@Local EntityRenderState state
		//?}
	) {
		if(EntityEvents.ALLOW_RENDER.dispatch(new EntityEvents.AllowRender(state))) ci.cancel();
	}

	@Inject(
		//? if >=1.21.5 {
		/*method = "render(Lnet/minecraft/client/render/entity/state/EntityRenderState;DDDLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/EntityRenderer;)V",
		*///?} else {
		method = "render(Lnet/minecraft/entity/Entity;DDDFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/EntityRenderer;)V",
		//?}
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/util/math/MatrixStack;push()V"
		),
		allow = 1
	)
	public void nobaaddons$preEntityRender(
		//? if >=1.21.5 {
		/*EntityRenderState state,
		*///?} else {
		Entity entity,
		//?}
		double x, double y, double z,
		//? if <1.21.5 {
		float tickDelta,
		//?}
		MatrixStack matrices, VertexConsumerProvider vertexConsumers,
		int light,
		EntityRenderer<?, ?> renderer,
		CallbackInfo ci/*? if <1.21.5 {*/,
		@Local EntityRenderState state,
		@Share(value = "renderState", namespace = "nobaaddons") LocalRef<EntityRenderState> stateRef
		//?}
	) {
		//? if >=1.21.5 {
		/*float tickDelta = MinecraftClient.getInstance().getRenderTickCounter().getTickProgress(true);
		Entity entity = ((EntityRenderStateDuck) state).nobaaddons$getEntity();
		*///?} else {
		stateRef.set(state);
		//?}
		if(entity != null) EntityEvents.PRE_RENDER.dispatch(new EntityEvents.Render(entity, state, tickDelta));
	}

	@Inject(
		//? if >=1.21.5 {
		/*method = "render(Lnet/minecraft/client/render/entity/state/EntityRenderState;DDDLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/EntityRenderer;)V",
		*///?} else {
		method = "render(Lnet/minecraft/entity/Entity;DDDFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/EntityRenderer;)V",
		//?}
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/util/math/MatrixStack;pop()V"
		),
		allow = 1
	)
	public void nobaaddons$postEntityRender(
		//? if >=1.21.5 {
		/*EntityRenderState state,
		*///?} else {
		Entity entity,
		//?}
		double x, double y, double z,
		//? if <1.21.5 {
		float tickDelta,
		//?}
		MatrixStack matrices,VertexConsumerProvider vertexConsumers,
		int light,
		EntityRenderer<?, ?> renderer,
		CallbackInfo ci/*? if <1.21.5 {*/,
		// either this mcdev plugin is bullshitting me, or @Local genuinely cannot find the render state
		// that's within the same try block as this pop call. i don't know, and it's not that much effort
		// to just work around the issue one way or another.
		@Share(value = "renderState", namespace = "nobaaddons") LocalRef<EntityRenderState> stateRef
		//?}
	) {
		//? if >=1.21.5 {
		/*float tickDelta = MinecraftClient.getInstance().getRenderTickCounter().getTickProgress(true);
		Entity entity = ((EntityRenderStateDuck) state).nobaaddons$getEntity();
		*///?}
		if(entity != null) EntityEvents.POST_RENDER.dispatch(new EntityEvents.Render(entity, stateRef.get(), tickDelta));
	}
}