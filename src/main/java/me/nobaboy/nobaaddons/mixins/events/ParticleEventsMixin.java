package me.nobaboy.nobaaddons.mixins.events;

import me.nobaboy.nobaaddons.events.impl.render.ParticleEvents;
import me.nobaboy.nobaaddons.utils.NobaVec;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
abstract class ParticleEventsMixin {
	@Inject(method = "onParticle", at = @At("RETURN"), cancellable = true)
	private void nobaaddons$onParticle(ParticleS2CPacket packet, CallbackInfo ci) {
		var location = new NobaVec(packet.getX(), packet.getY(), packet.getZ());
		var offset = new NobaVec(packet.getOffsetX(), packet.getOffsetY(), packet.getOffsetZ()).roundTo(2);

		var forceSpawn = /*? if >=1.21.4 {*/packet.shouldForceSpawn()/*?} else {*//*packet.isLongDistance()*//*?}*/;
		var allow = new ParticleEvents.AllowParticle(packet.getParameters().getType(), location, packet.getCount(), packet.getSpeed(), offset, forceSpawn);
		if(ParticleEvents.ALLOW_PARTICLE.dispatch(allow)) {
			ci.cancel();
			return;
		}

		ParticleEvents.PARTICLE.dispatch(new ParticleEvents.Particle(packet.getParameters().getType(), location, packet.getCount(), packet.getSpeed(), offset, forceSpawn));
	}
}
