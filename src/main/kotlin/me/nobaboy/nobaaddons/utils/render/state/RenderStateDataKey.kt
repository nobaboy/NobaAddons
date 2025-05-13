package me.nobaboy.nobaaddons.utils.render.state

import me.nobaboy.nobaaddons.ducks.StateDataHolder
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
	fun getData(holder: StateDataHolder): RenderStateDataKey<T>.Value =
		holder.`nobaaddons$getData`().getOrPut(this, ::Value) as RenderStateDataKey<T>.Value

	fun getData(state: EntityRenderState): RenderStateDataKey<T>.Value = getData(state as StateDataHolder)
	fun getData(entity: Entity): RenderStateDataKey<T>.Value = getData(entity as StateDataHolder)

	fun get(state: EntityRenderState): T = getData(state).value
	fun get(entity: Entity): T = getData(entity).value

	fun put(state: EntityRenderState, value: T) {
		getData(state).value = value
	}

	fun put(entity: Entity, value: T) {
		getData(entity).value = value
	}

	/**
	 * Copy the data stored under this key on the provided [Entity] to the provided [EntityRenderState]
	 */
	fun copyToRender(entity: Entity, state: EntityRenderState) {
		put(state, get(entity))
	}

	companion object {
		@JvmField val ENTITY: RenderStateDataKey<Entity?> = RenderStateDataKey<Entity?> { null }

		operator fun StateDataHolder.contains(key: RenderStateDataKey<*>): Boolean = `nobaaddons$getData`().contains(key)
		operator fun Entity.contains(key: RenderStateDataKey<*>): Boolean = (this as StateDataHolder).contains(key)
		operator fun EntityRenderState.contains(key: RenderStateDataKey<*>): Boolean = (this as StateDataHolder).contains(key)
	}
}