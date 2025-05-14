package me.nobaboy.nobaaddons.utils.render

import me.nobaboy.nobaaddons.ducks.StateDataHolder
import net.minecraft.client.render.entity.state.EntityRenderState
import net.minecraft.entity.Entity
import net.minecraft.util.math.MathHelper
import kotlin.collections.contains

/**
 * Utility class providing a way to store data on [Entity] and [EntityRenderState] instances, and facilitating
 * transferring data from an entity to its associated render state.
 *
 * Instances of this class are assumed to be unique keys, and as such, [equals] will *never* return `true` when
 * comparing two different class instances, and as such can safely be used as keys in a [Map].
 *
 * @see me.nobaboy.nobaaddons.events.impl.render.RenderStateUpdateEvent
 */
class EntityDataKey<T>(private val initialValue: () -> T) {
	// precompute the hash to avoid needing to do this relatively expensive operation every map lookup
	private val id = MathHelper.randomUuid().hashCode()

	override fun equals(other: Any?): Boolean = this === other
	override fun hashCode(): Int = id

	/**
	 * Class containing the stored value for the associated [EntityDataKey].
	 *
	 * This class is not designed to be manually constructed.
	 */
	inner class Value(@Volatile var value: T = initialValue()) {
		fun clear() {
			value = initialValue()
		}
	}

	/**
	 * Get the [Value] instance stored in the provided [StateDataHolder]
	 */
	@Suppress("UNCHECKED_CAST")
	fun getData(holder: StateDataHolder): EntityDataKey<T>.Value =
		holder.`nobaaddons$getData`().getOrPut(this, ::Value) as EntityDataKey<T>.Value

	/**
	 * Convenience overload for `getData(state as StateDataHolder)`
	 */
	fun getData(state: EntityRenderState): EntityDataKey<T>.Value = getData(state as StateDataHolder)

	/**
	 * Convenience overload for `getData(entity as StateDataHolder)`
	 */
	fun getData(entity: Entity): EntityDataKey<T>.Value = getData(entity as StateDataHolder)

	/**
	 * Returns the current stored value for the provided [render state][state]
	 */
	fun get(state: EntityRenderState): T = getData(state).value

	/**
	 * Returns the current stored value for the provided [entity]
	 */
	fun get(entity: Entity): T = getData(entity).value

	/**
	 * Set the stored value for this key on the provided [render state][state]
	 */
	fun put(state: EntityRenderState, value: T) {
		getData(state).value = value
	}

	/**
	 * Set the stored value for this key on the provided [entity]
	 */
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
		operator fun StateDataHolder.contains(key: EntityDataKey<*>): Boolean = `nobaaddons$getData`().contains(key)
		operator fun Entity.contains(key: EntityDataKey<*>): Boolean = (this as StateDataHolder).contains(key)
		operator fun EntityRenderState.contains(key: EntityDataKey<*>): Boolean = (this as StateDataHolder).contains(key)

		/**
		 * Convenience constructor creating a [EntityDataKey] that returns `null` as its initial value
		 */
		@JvmStatic
		fun <T> nullable() = EntityDataKey<T?> { null }
	}
}