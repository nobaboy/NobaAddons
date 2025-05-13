package me.nobaboy.nobaaddons.mixins.duckimpl;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import me.nobaboy.nobaaddons.ducks.EntityRenderStateDuck;
import me.nobaboy.nobaaddons.utils.render.state.RenderStateDataKey;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Map;

@SuppressWarnings("unused")
@Mixin(EntityRenderState.class)
class EntityRenderStateDuckImpl implements EntityRenderStateDuck {
	@Unique
	private final Map<RenderStateDataKey<?>, RenderStateDataKey<?>.Value> nobaaddons$data = new Object2ObjectArrayMap<>();

	@Override
	public Map<RenderStateDataKey<?>, RenderStateDataKey<?>.Value> nobaaddons$getData() {
		return nobaaddons$data;
	}

	@Override
	public @Nullable Entity nobaaddons$getEntity() {
		return RenderStateDataKey.ENTITY.get((EntityRenderState)(Object)this);
	}
}
