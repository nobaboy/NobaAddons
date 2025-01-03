package me.nobaboy.nobaaddons.mixins.events;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import me.nobaboy.nobaaddons.events.SoundEvents;
import me.nobaboy.nobaaddons.utils.NobaVec;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientPlayNetworkHandler.class)
abstract class SoundEventsMixin {
	@WrapWithCondition(
		method = "onPlaySound",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/world/ClientWorld;playSound(Lnet/minecraft/entity/player/PlayerEntity;DDDLnet/minecraft/registry/entry/RegistryEntry;Lnet/minecraft/sound/SoundCategory;FFJ)V"
		)
	)
	public boolean nobaaddons$onPlaySound(ClientWorld instance, @Nullable PlayerEntity source, double x, double y, double z, RegistryEntry<SoundEvent> sound, SoundCategory category, float volume, float pitch, long seed) {
		var key = sound.getKeyOrValue();
		var id = key.left()
			.map(RegistryKey::getValue)
			.orElseGet(() -> key.right()
				//? if >=1.21.2 {
				.map(SoundEvent::id)
				//?} else {
				/*.map(SoundEvent::getId)
				*///?}
				.orElseThrow());

		var location = new NobaVec(x, y, z);

		var allowEvent = new SoundEvents.AllowSound(id, location, pitch, volume);
		var soundEvent = new SoundEvents.Sound(id, category, location, pitch, volume);

		SoundEvents.ALLOW_SOUND.invoke(allowEvent);
		if(allowEvent.isCanceled()) {
			SoundEvents.SOUND_CANCELED.invoke(soundEvent);
			return false;
		}

		SoundEvents.SOUND.invoke(soundEvent);
		return true;
	}
}