package me.nobaboy.nobaaddons.utils.render.state

import me.nobaboy.nobaaddons.ducks.EntityRenderStateDuck
import net.minecraft.client.render.entity.state.EntityRenderState
import net.minecraft.entity.Entity

class RenderStateDataKey<T>(private val initialValue: () -> T) {
	inner class Value(var value: T = initialValue())

	@Suppress("UNCHECKED_CAST")
	fun getData(state: EntityRenderState): RenderStateDataKey<T>.Value =
		(state as EntityRenderStateDuck).`nobaaddons$getData`().getOrPut(this, ::Value) as RenderStateDataKey<T>.Value

	fun get(state: EntityRenderState): T = getData(state).value

	fun put(state: EntityRenderState, value: T) {
		getData(state).value = value
	}

	companion object {
		@JvmField val ENTITY: RenderStateDataKey<Entity?> = RenderStateDataKey<Entity?> { null }
	}
}