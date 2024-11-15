package me.nobaboy.nobaaddons.mixins.events;

import me.nobaboy.nobaaddons.data.jsonobjects.ParticleData;
import me.nobaboy.nobaaddons.events.ParticleEvent;
import me.nobaboy.nobaaddons.utils.NobaVec;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ParticleEventMixin {
	@Inject(method = "onParticle", at = @At("RETURN"))
	private void onParticle(ParticleS2CPacket packet, CallbackInfo ci) {
		var location = new NobaVec(packet.getX(), packet.getY(), packet.getZ());
		var offset = new NobaVec(packet.getOffsetX(), packet.getOffsetY(), packet.getOffsetZ());

		var particle = new ParticleData(packet.getParameters().getType(), location, packet.getCount(), packet.getSpeed(), offset, packet.isLongDistance());

		ParticleEvent.EVENT.invoker().onParticle(particle);
	}
}
