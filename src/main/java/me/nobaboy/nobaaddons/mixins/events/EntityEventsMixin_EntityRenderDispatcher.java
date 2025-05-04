package me.nobaboy.nobaaddons.mixins.events;

//? if >=1.21.5 {
/*import me.nobaboy.nobaaddons.ducks.EntityStateCaptureDuck;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.state.EntityRenderState;
*///?}

import me.nobaboy.nobaaddons.events.impl.client.EntityEvents;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? if >=1.21.2 {
// For whatever reason, the MC Dev plugin complains about this, but only on 1.21.2+.
// This is despite the fact that this is, in fact, a valid injector.
@SuppressWarnings("InvalidInjectorMethodSignature")
//?}
@Mixin(EntityRenderDispatcher.class)
abstract class EntityEventsMixin_EntityRenderDispatcher {
	@Inject(
		//? if >=1.21.5 {
		/*method = "render(Lnet/minecraft/client/render/entity/state/EntityRenderState;DDDLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/EntityRenderer;)V",
		*///?} else if >=1.21.2 {
		method = "render(Lnet/minecraft/entity/Entity;DDDFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/EntityRenderer;)V",
		//?} else {
		/*method = "render",
		*///?}
		at = @At("HEAD"),
		cancellable = true
	)
	public void nobaaddons$cancelEntityRender(
		//? if >=1.21.5 {
		/*EntityRenderState state,
		*///?} else {
		Entity entity,
		//?}
		double x, double y, double z,
		//? if <1.21.2 {
		/*float yaw,
		*///?}
		//? if <1.21.5 {
		float tickDelta,
		//?}
		MatrixStack matrices,VertexConsumerProvider vertexConsumers,
		int light,
		//? if >=1.21.2 {
		EntityRenderer<?, ?> renderer,
		//?}
		CallbackInfo ci
	) {
		//? if >=1.21.5 {
		/*Entity entity = ((EntityStateCaptureDuck) state).nobaaddons$getEntity();
		*///?}
		if(entity != null && EntityEvents.ALLOW_RENDER.dispatch(new EntityEvents.AllowRender(entity))) ci.cancel();
	}

	@Inject(
		//? if >=1.21.5 {
		/*method = "render(Lnet/minecraft/client/render/entity/state/EntityRenderState;DDDLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/EntityRenderer;)V",
		*///?} else if >=1.21.2 {
		method = "render(Lnet/minecraft/entity/Entity;DDDFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/EntityRenderer;)V",
		//?} else {
		/*method = "render",
		*///?}
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
		//? if <1.21.2 {
		/*float yaw,
		*///?}
		//? if <1.21.5 {
		float tickDelta,
		//?}
		MatrixStack matrices,VertexConsumerProvider vertexConsumers,
		int light,
		//? if >=1.21.2 {
		EntityRenderer<?, ?> renderer,
		//?}
		CallbackInfo ci
	) {
		//? if >=1.21.5 {
		/*float tickDelta = MinecraftClient.getInstance().getRenderTickCounter().getTickProgress(true);
		Entity entity = ((EntityStateCaptureDuck) state).nobaaddons$getEntity();
		*///?}
		if(entity != null) EntityEvents.PRE_RENDER.dispatch(new EntityEvents.Render(entity, tickDelta));
	}

	@Inject(
		//? if >=1.21.5 {
		/*method = "render(Lnet/minecraft/client/render/entity/state/EntityRenderState;DDDLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/EntityRenderer;)V",
		*///?} else if >=1.21.2 {
		method = "render(Lnet/minecraft/entity/Entity;DDDFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/EntityRenderer;)V",
		//?} else {
		/*method = "render",
		*///?}
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
		//? if <1.21.2 {
		/*float yaw,
		*///?}
		//? if <1.21.5 {
		float tickDelta,
		//?}
		MatrixStack matrices,VertexConsumerProvider vertexConsumers,
		int light,
		//? if >=1.21.2 {
		EntityRenderer<?, ?> renderer,
		//?}
		CallbackInfo ci
	) {
		//? if >=1.21.5 {
		/*float tickDelta = MinecraftClient.getInstance().getRenderTickCounter().getTickProgress(true);
		Entity entity = ((EntityStateCaptureDuck) state).nobaaddons$getEntity();
		*///?}
		if(entity != null) EntityEvents.POST_RENDER.dispatch(new EntityEvents.Render(entity, tickDelta));
	}
}