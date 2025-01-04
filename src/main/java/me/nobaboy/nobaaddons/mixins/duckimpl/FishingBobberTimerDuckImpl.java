package me.nobaboy.nobaaddons.mixins.duckimpl;

import me.nobaboy.nobaaddons.ducks.FishingBobberTimerDuck;
import net.minecraft.entity.projectile.FishingBobberEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.time.Instant;

@SuppressWarnings("unused")
@Mixin(FishingBobberEntity.class)
abstract class FishingBobberTimerDuckImpl implements FishingBobberTimerDuck {
	private @Unique Instant nobaaddons$spawnedAt;

	@Override
	public Instant nobaaddons$spawnedAt() {
		return nobaaddons$spawnedAt;
	}

	@Override
	public void nobaaddons$markSpawnTime() {
		this.nobaaddons$spawnedAt = Instant.now();
	}
}
