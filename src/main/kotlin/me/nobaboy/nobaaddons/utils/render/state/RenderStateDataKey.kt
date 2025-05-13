package me.nobaboy.nobaaddons.utils.render.state

import me.nobaboy.nobaaddons.ducks.EntityRenderStateDuck
import net.minecraft.client.render.entity.state.EntityRenderState
import net.minecraft.entity.Entity
import net.minecraft.util.math.MathHelper

class RenderStateDataKey<T>(private val initialValue: () -> T) {
	private val id = MathHelper.randomUuid().hashCode()

	override fun equals(other: Any?): Boolean = this === other
	override fun hashCode(): Int = id

	inner class Value(var value: T = initialValue()) {
		fun clear() {
			value = initialValue()
		}
	}

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