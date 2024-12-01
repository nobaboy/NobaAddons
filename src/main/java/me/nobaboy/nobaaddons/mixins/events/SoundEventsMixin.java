package me.nobaboy.nobaaddons.mixins.events;

import me.nobaboy.nobaaddons.events.SoundEvents;
import me.nobaboy.nobaaddons.utils.NobaVec;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.registry.RegistryKey;
import net.minecraft.sound.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class SoundEventsMixin {
	@Inject(
		method = "onPlaySound",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;playSound(Lnet/minecraft/entity/player/PlayerEntity;DDDLnet/minecraft/registry/entry/RegistryEntry;Lnet/minecraft/sound/SoundCategory;FFJ)V"),
		cancellable = true
	)
	public void nobaaddons$onPlaySound(PlaySoundS2CPacket packet, CallbackInfo ci) {
		var sound = packet.getSound().getKeyOrValue();
		var id = sound.left()
			.map(RegistryKey::getValue)
			.orElseGet(() -> sound.right()
				//? if >=1.21.2 {
				.map(SoundEvent::id)
				//?} else {
				/*.map(SoundEvent::getId)*/
				//?}
				.orElseThrow());

		var location = new NobaVec(packet.getX(), packet.getY(), packet.getZ());

		var allow = new SoundEvents.AllowSound(id, location, packet.getPitch(), packet.getVolume());
		var soundEvent = new SoundEvents.Sound(id, packet.getCategory(), location, packet.getPitch(), packet.getVolume());
		SoundEvents.ALLOW_SOUND.invoke(allow);
		if(allow.isCanceled()) {
			SoundEvents.SOUND_CANCELED.invoke(soundEvent);
			ci.cancel();
			return;
		}

		SoundEvents.SOUND.invoke(soundEvent);
	}
}