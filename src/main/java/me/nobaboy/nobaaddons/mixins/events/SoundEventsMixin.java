package me.nobaboy.nobaaddons.mixins.events;

//? if >=1.21.5 {
/*import net.minecraft.entity.Entity;
*///?} else {
import net.minecraft.entity.player.PlayerEntity;
//?}

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import me.nobaboy.nobaaddons.events.impl.client.SoundEvents;
import me.nobaboy.nobaaddons.utils.NobaVec;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
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
			//? if >=1.21.5 {
			/*target = "Lnet/minecraft/client/world/ClientWorld;playSound(Lnet/minecraft/entity/Entity;DDDLnet/minecraft/registry/entry/RegistryEntry;Lnet/minecraft/sound/SoundCategory;FFJ)V"
			*///?} else {
			target = "Lnet/minecraft/client/world/ClientWorld;playSound(Lnet/minecraft/entity/player/PlayerEntity;DDDLnet/minecraft/registry/entry/RegistryEntry;Lnet/minecraft/sound/SoundCategory;FFJ)V"
			//?}
		)
	)
	public boolean nobaaddons$onPlaySound(
		ClientWorld instance,
		@Nullable /*? if >=1.21.5 {*//*Entity*//*?} else {*/PlayerEntity/*?}*/ source,
		double x, double y, double z,
		RegistryEntry<SoundEvent> sound,
		SoundCategory category,
		float volume,
		float pitch,
		long seed
	) {
		var key = sound.getKeyOrValue();
		var id = key.left()
			.map(RegistryKey::getValue)
			.orElseGet(() -> key.right().map(SoundEvent::id).orElseThrow());

		var location = new NobaVec(x, y, z);

		var allowEvent = new SoundEvents.AllowSound(id, location, pitch, volume);
		var soundEvent = new SoundEvents.Sound(id, category, location, pitch, volume);

		if(SoundEvents.ALLOW_SOUND.dispatch(allowEvent)) {
			SoundEvents.SOUND_CANCELED.dispatch(soundEvent);
			return false;
		}

		SoundEvents.SOUND.dispatch(soundEvent);
		return true;
	}
}