package me.nobaboy.nobaaddons.mixins.events;

import me.nobaboy.nobaaddons.events.impl.entity.EntityTickEvent;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
abstract class EntityTickEventMixin {
	@Shadow
	public abstract World getEntityWorld();

	@Inject(method = "tick", at = @At("TAIL"))
	public void nobaaddons$onEntityTick(CallbackInfo ci) {
		if(!getEntityWorld().isClient()) return;
		EntityTickEvent.EVENT.dispatch(new EntityTickEvent((Entity)(Object)this));
	}
}
