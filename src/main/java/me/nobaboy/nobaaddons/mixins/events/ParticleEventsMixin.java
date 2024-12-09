package me.nobaboy.nobaaddons.mixins.events;

import me.nobaboy.nobaaddons.events.ParticleEvents;
import me.nobaboy.nobaaddons.utils.NobaVec;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ParticleEventsMixin {
	@Inject(method = "onParticle", at = @At("RETURN"), cancellable = true)
	private void onParticle(ParticleS2CPacket packet, CallbackInfo ci) {
		var location = new NobaVec(packet.getX(), packet.getY(), packet.getZ());
		var offset = new NobaVec(packet.getOffsetX(), packet.getOffsetY(), packet.getOffsetZ()).round(2);

		var forceSpawn = /*? if >=1.21.4 {*/packet.shouldForceSpawn()/*?} else {*//*packet.isLongDistance()*//*?}*/;
		var allow = new ParticleEvents.AllowParticle(packet.getParameters().getType(), location, packet.getCount(), packet.getSpeed(), offset, forceSpawn);
		ParticleEvents.ALLOW_PARTICLE.invoke(allow);
		if(allow.isCanceled()) {
			ci.cancel();
			return;
		}

		ParticleEvents.PARTICLE.invoke(new ParticleEvents.Particle(packet.getParameters().getType(), location, packet.getCount(), packet.getSpeed(), offset, forceSpawn));
	}
}
