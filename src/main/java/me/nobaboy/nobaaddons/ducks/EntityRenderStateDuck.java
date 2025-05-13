package me.nobaboy.nobaaddons.ducks;

import me.nobaboy.nobaaddons.utils.render.state.RenderStateDataKey;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface EntityRenderStateDuck {
	Map<RenderStateDataKey<?>, RenderStateDataKey<?>.Value> nobaaddons$getData();

	@Deprecated
	@Nullable Entity nobaaddons$getEntity();

	@Deprecated
	void nobaaddons$setEntity(@Nullable Entity entity);
}
