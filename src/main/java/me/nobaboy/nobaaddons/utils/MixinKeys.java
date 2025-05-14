package me.nobaboy.nobaaddons.utils;

import me.nobaboy.nobaaddons.events.impl.render.RenderStateUpdateEvent;
import me.nobaboy.nobaaddons.utils.render.EntityDataKey;

public final class MixinKeys {
	private MixinKeys() {
		throw new UnsupportedOperationException();
	}

	public static final EntityDataKey<Boolean> RENDER_ORIGINAL_ENTITY_NAME = new EntityDataKey<>(() -> true);

	static {
		RenderStateUpdateEvent.EVENT.register(event -> event.copyToRender(RENDER_ORIGINAL_ENTITY_NAME));
	}
}
