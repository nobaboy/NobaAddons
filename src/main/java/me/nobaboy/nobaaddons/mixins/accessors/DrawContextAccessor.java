package me.nobaboy.nobaaddons.mixins.accessors;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.VertexConsumerProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

//? if >=1.21.2 {
@Mixin(DrawContext.class)
public interface DrawContextAccessor {
	@Accessor
	VertexConsumerProvider.Immediate getVertexConsumers();
}
//?}
