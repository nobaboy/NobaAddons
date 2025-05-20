package me.nobaboy.nobaaddons.ducks;

import me.nobaboy.nobaaddons.utils.render.EntityDataKey;

import java.util.Map;

/**
 * @see EntityDataKey
 */
public interface StateDataHolder {
	@SuppressWarnings("unused") // incorrect
	Map<EntityDataKey<?>, EntityDataKey<?>.Value> nobaaddons$getData();
}
