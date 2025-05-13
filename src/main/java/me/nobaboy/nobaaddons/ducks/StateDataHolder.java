package me.nobaboy.nobaaddons.ducks;

import me.nobaboy.nobaaddons.utils.render.state.RenderStateDataKey;

import java.util.Map;

public interface StateDataHolder {
	@SuppressWarnings("unused") // incorrect
	Map<RenderStateDataKey<?>, RenderStateDataKey<?>.Value> nobaaddons$getData();
}
