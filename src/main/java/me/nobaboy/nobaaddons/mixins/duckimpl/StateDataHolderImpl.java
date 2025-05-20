package me.nobaboy.nobaaddons.mixins.duckimpl;

import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import me.nobaboy.nobaaddons.ducks.StateDataHolder;
import me.nobaboy.nobaaddons.utils.render.EntityDataKey;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Map;

@SuppressWarnings("unused")
@Mixin({Entity.class, EntityRenderState.class})
class StateDataHolderImpl implements StateDataHolder {
	// TODO it might be worth changing this to an Reference2ObjectOpenHashMap at some point if we start consistently
	//      adding enough things to this
	@Unique
	private final Reference2ObjectArrayMap<EntityDataKey<?>, EntityDataKey<?>.Value> nobaaddons$stateData = new Reference2ObjectArrayMap<>(32);

	@Override
	public Map<EntityDataKey<?>, EntityDataKey<?>.Value> nobaaddons$getData() {
		return nobaaddons$stateData;
	}
}
