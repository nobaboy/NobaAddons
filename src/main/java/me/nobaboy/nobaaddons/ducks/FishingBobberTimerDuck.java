package me.nobaboy.nobaaddons.ducks;

import java.time.Instant;

public interface FishingBobberTimerDuck {
	Instant nobaaddons$spawnedAt();
	void nobaaddons$markSpawnTime();
}
