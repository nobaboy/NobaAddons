package me.nobaboy.nobaaddons.mixins.transformers;

import me.nobaboy.nobaaddons.data.jsonobjects.ParticleData;
import me.nobaboy.nobaaddons.events.ParticleEvent;
import me.nobaboy.nobaaddons.events.PlaySoundEvent;
import me.nobaboy.nobaaddons.utils.NobaVec;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.registry.RegistryKey;
import net.minecraft.sound.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
	@Inject(method = "onPlaySound", at = @At("HEAD"), cancellable = true)
	public void onPlaySound(PlaySoundS2CPacket packet, CallbackInfo ci) {
		var sound = packet.getSound().getKeyOrValue();
		var id = sound.left().map(RegistryKey::getValue).orElseGet(() -> sound.right().map(SoundEvent::id).orElseThrow());
		var location = new NobaVec(packet.getX(), packet.getY(), packet.getZ());

		if(!PlaySoundEvent.ALLOW_SOUND.invoker().onSound(id, location, packet.getPitch(), packet.getVolume())) {
			ci.cancel();
		}

		PlaySoundEvent.SOUND.invoker().onSound(id, location, packet.getPitch(), packet.getVolume());
	}

	@Inject(method = "onParticle", at = @At("RETURN"))
	private void onParticle(ParticleS2CPacket packet, CallbackInfo ci) {
		var location = new NobaVec(packet.getX(), packet.getY(), packet.getZ());
		var offset = new NobaVec(packet.getOffsetX(), packet.getOffsetY(), packet.getOffsetZ());

		var particle = new ParticleData(packet.getParameters().getType(), location, packet.getCount(), packet.getSpeed(), offset, packet.isLongDistance());

		ParticleEvent.EVENT.invoker().onParticle(particle);
	}
}
