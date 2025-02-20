package me.nobaboy.nobaaddons.mixins.events;

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
		//? if >=1.21.2 {
		method = "render(Lnet/minecraft/entity/Entity;DDDFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/EntityRenderer;)V",
		//?} else {
		/*method = "render",
		*///?}
		at = @At("HEAD"),
		cancellable = true
	)
	public void nobaaddons$cancelEntityRender(
		Entity entity,
		double x, double y, double z,
		//? if <1.21.2 {
		/*float yaw,
		*///?}
		float tickDelta,
		MatrixStack matrices,
		VertexConsumerProvider vertexConsumers,
		int light,
		//? if >=1.21.2 {
		EntityRenderer<?, ?> renderer,
		//?}
		CallbackInfo ci
	) {
		if(EntityEvents.ALLOW_RENDER.invoke(new EntityEvents.AllowRender(entity))) ci.cancel();
	}

	@Inject(
		//? if >=1.21.2 {
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
		Entity entity,
		double x, double y, double z,
		//? if <1.21.2 {
		/*float yaw,
		*///?}
		float tickDelta,
		MatrixStack matrices,
		VertexConsumerProvider vertexConsumers,
		int light,
		//? if >=1.21.2 {
		EntityRenderer<?, ?> renderer,
		//?}
		CallbackInfo ci
	) {
		EntityEvents.PRE_RENDER.invoke(new EntityEvents.Render(entity, tickDelta));
	}

	@Inject(
		//? if >=1.21.2 {
		method = "render(Lnet/minecraft/entity/Entity;DDDFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/EntityRenderer;)V",
		//?} else {
		/*method = "render",
		*///?}
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/util/math/MatrixStack;pop()V",
			shift = At.Shift.AFTER
		),
		allow = 1
	)
	public void nobaaddons$postEntityRender(
		Entity entity,
		double x, double y, double z,
		//? if <1.21.2 {
		/*float yaw,
		*///?}
		float tickDelta,
		MatrixStack matrices,
		VertexConsumerProvider vertexConsumers,
		int light,
		//? if >=1.21.2 {
		EntityRenderer<?, ?> renderer,
		//?}
		CallbackInfo ci
	) {
		EntityEvents.POST_RENDER.invoke(new EntityEvents.Render(entity, tickDelta));
	}
}
