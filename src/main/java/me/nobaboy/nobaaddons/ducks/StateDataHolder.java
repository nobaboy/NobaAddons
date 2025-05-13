package me.nobaboy.nobaaddons.ducks;

import me.nobaboy.nobaaddons.utils.render.state.EntityDataKey;

import java.util.Map;

public interface StateDataHolder {
	@SuppressWarnings("unused") // incorrect
	Map<EntityDataKey<?>, EntityDataKey<?>.Value> nobaaddons$getData();
}
