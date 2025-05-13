package me.nobaboy.nobaaddons.mixins.duckimpl;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import me.nobaboy.nobaaddons.ducks.StateDataHolder;
import me.nobaboy.nobaaddons.utils.render.state.EntityDataKey;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Map;

@SuppressWarnings("UnusedMixin")
@Mixin(Entity.class)
class StateDataHolderImpl$Entity implements StateDataHolder {
	@Unique
	private final Map<EntityDataKey<?>, EntityDataKey<?>.Value> nobaaddons$stateData = new Object2ObjectArrayMap<>();

	@Override
	public Map<EntityDataKey<?>, EntityDataKey<?>.Value> nobaaddons$getData() {
		return nobaaddons$stateData;
	}
}
