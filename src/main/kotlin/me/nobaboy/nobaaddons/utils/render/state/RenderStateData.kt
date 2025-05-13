package me.nobaboy.nobaaddons.utils.render.state

import net.minecraft.client.render.entity.state.EntityRenderState
import net.minecraft.util.math.MathHelper

class RenderStateData<T>(private val initialValue: () -> T) {
	val id = MathHelper.randomUuid().toString()
	var value = initialValue()

	fun reset() {
		value = initialValue()
	}

	companion object {
		fun <T> EntityRenderState.get(data: RenderStateData<T>): T {
			TODO()
		}
	}
}