package me.nobaboy.nobaaddons.mixins.events;

import me.nobaboy.nobaaddons.events.impl.client.EntityEvents;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
abstract class EntityEventsMixin_Entity {
	@Inject(method = "startRiding(Lnet/minecraft/entity/Entity;Z)Z", at = @At("RETURN"))
	public void nobaaddons$onStartRiding(Entity vehicle, boolean force, CallbackInfoReturnable<Boolean> cir) {
		if(cir.getReturnValue()) EntityEvents.VEHICLE_CHANGE.invoke(new EntityEvents.VehicleChange((Entity) (Object) this, vehicle));
	}
}
