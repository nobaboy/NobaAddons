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
 * Instances of this class are assumed to be unique keys suitable for use in a [Map], and as such [equals] will
 * *never* return `true` when comparing two different class instances.
 *
 * ## Example
 *
 * ```kt
 * val DATA = EntityDataKey<Type> { ... }
 *
 * // copy data stored on an entity to the associated render state
 * // note that this does not properly handle mutable types (such as MutableList and MutableMap);
 * // you're responsible for properly handling such types as necessary.
 * RenderStateUpdateEvent.EVENT.register { it.copyToRender(DATA) }
 *
 * // in some event somewhere...
 * DATA.get(entity) // get the stored value on an entity
 * DATA.get(state) // or from a render state
 *
 * DATA.put(entity, ...) // set the stored value
 * ```
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
	inner class Value(@Volatile var value: T = initialValue())

	/**
	 * Get the [Value] instance stored in the provided [StateDataHolder], or `null` if none exists
	 */
	@Suppress("UNCHECKED_CAST")
	private fun getDataOrNull(holder: StateDataHolder): EntityDataKey<T>.Value? =
		holder.`nobaaddons$getData`()[this] as EntityDataKey<T>.Value?

	/**
	 * Get the [Value] instance stored in the provided [StateDataHolder], creating it if one doesn't exist
	 */
	@Suppress("UNCHECKED_CAST")
	private fun getData(holder: StateDataHolder): EntityDataKey<T>.Value =
		holder.`nobaaddons$getData`().getOrPut(this, ::Value) as EntityDataKey<T>.Value

	/**
	 * Returns the current stored value for the provided [render state][state]
	 */
	fun get(state: EntityRenderState): T = getData(state.asDataHolder).value

	/**
	 * Returns the current stored value for the provided [entity]
	 */
	fun get(entity: Entity): T = getData(entity.asDataHolder).value

	/**
	 * Returns the current stored value for the provided [render state][state]
	 */
	fun getOrNull(state: EntityRenderState): T? = getDataOrNull(state.asDataHolder)?.value

	/**
	 * Returns the current stored value for the provided [entity]
	 */
	fun getOrNull(entity: Entity): T? = getDataOrNull(entity.asDataHolder)?.value

	/**
	 * Set the stored value for this key on the provided [render state][state]
	 */
	fun put(state: EntityRenderState, value: T) {
		getData(state.asDataHolder).value = value
	}

	/**
	 * Set the stored value for this key on the provided [entity]
	 */
	fun put(entity: Entity, value: T) {
		getData(entity.asDataHolder).value = value
	}

	/**
	 * Reset the stored value for this key on the provided [render state][state] to the initial value for this key
	 */
	fun reset(state: EntityRenderState) {
		getData(state.asDataHolder).value = initialValue()
	}

	/**
	 * Reset the stored value for this key on the provided [entity] to the initial value for this key
	 */
	fun reset(entity: Entity) {
		getData(entity.asDataHolder).value = initialValue()
	}

	/**
	 * Copy the data stored under this key on the provided [Entity] to the provided [EntityRenderState]
	 */
	fun copyToRender(entity: Entity, state: EntityRenderState) {
		if(this !in entity) return
		put(state, get(entity))
	}

	companion object {
		operator fun StateDataHolder.contains(key: EntityDataKey<*>): Boolean = `nobaaddons$getData`().contains(key)
		operator fun Entity.contains(key: EntityDataKey<*>): Boolean = (this as StateDataHolder).contains(key)
		operator fun EntityRenderState.contains(key: EntityDataKey<*>): Boolean = (this as StateDataHolder).contains(key)

		private inline val Entity.asDataHolder get() = this as StateDataHolder
		private inline val EntityRenderState.asDataHolder get() = this as StateDataHolder

		/**
		 * Convenience constructor creating a [EntityDataKey] that returns `null` as its initial value
		 */
		@JvmStatic
		fun <T> nullable() = EntityDataKey<T?> { null }
	}
}