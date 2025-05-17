package me.nobaboy.nobaaddons.mixins.duckimpl;

//? if >=1.21.2 {
import me.nobaboy.nobaaddons.ducks.EntityStateCaptureDuck;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@SuppressWarnings("unused")
@Mixin(EntityRenderState.class)
abstract class EntityStateCaptureDuckImpl implements EntityStateCaptureDuck {
	// TODO drop 1.21.1 and move away from this entity capture logic
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
//?}
