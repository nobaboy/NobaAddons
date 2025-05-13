package me.nobaboy.nobaaddons.mixins.duckimpl;

import me.nobaboy.nobaaddons.ducks.EntityRenderStateDuck;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@SuppressWarnings("unused")
@Mixin(EntityRenderState.class)
abstract class EntityRenderStateDuckImpl implements EntityRenderStateDuck {
	private @Unique @Nullable Entity nobaaddons$entity;

	@Override
	public @Nullable Entity nobaaddons$getEntity() {
		return nobaaddons$entity;
	}

	@Override
	public void nobaaddons$setEntity(@Nullable Entity entity) {
		nobaaddons$entity = entity;
	}
}
