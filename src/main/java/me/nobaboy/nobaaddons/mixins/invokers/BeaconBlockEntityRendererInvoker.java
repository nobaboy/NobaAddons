package me.nobaboy.nobaaddons.mixins.invokers;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BeaconBlockEntityRenderer.class)
public interface BeaconBlockEntityRendererInvoker {
	@Invoker
	static void invokeRenderBeam(MatrixStack matrices, VertexConsumerProvider vertexConsumers, float tickDelta, long worldTime, int yOffset, int maxY, int color) {
		throw new UnsupportedOperationException();
	}
}