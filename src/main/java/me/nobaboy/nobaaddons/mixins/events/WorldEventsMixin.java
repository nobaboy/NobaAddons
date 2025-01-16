package me.nobaboy.nobaaddons.mixins.events;

import me.nobaboy.nobaaddons.events.impl.client.WorldEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
abstract class WorldEventsMixin {
	@Inject(method = "setWorld", at = @At("TAIL"))
	public void nobaaddons$onSetWorld(ClientWorld world, CallbackInfo ci) {
		if(world != null) {
			WorldEvents.POST_LOAD.invoke(new WorldEvents.WorldLoadEvent(world));
		}
	}
}
