package me.nobaboy.nobaaddons.events

import me.nobaboy.nobaaddons.events.internal.EventDispatcher
import net.minecraft.entity.Entity
import net.minecraft.text.Text

object EntityNametagRenderEvents {
	/**
	 * Event invoked to determine if an [Entity]'s nametag should be visible
	 */
	@JvmField val VISIBILITY = EventDispatcher<Visibility>()

	/**
	 * Event invoked when an [Entity]'s nametag is being rendered, allowing for adding new nametag line(s)
	 * to be rendered.
	 *
	 * Note that the provided [Nametag.tags] list also contains the entity's name, and as such will need to be
	 * removed if this is undesired.
	 *
	 * Also note that adding new lines to player nametags will currently cause the scoreboard objective
	 * in the below name slot (if any) to render multiple times.
	 */
	@JvmField val EVENT = EventDispatcher<Nametag>()

	data class Visibility(val entity: Entity, var shouldRender: Boolean)

	data class Nametag @JvmOverloads constructor(
		val entity: Entity,
		/**
		 * A list of [Text] to add under the entity's nametag
		 */
		val tags: MutableList<Text> = mutableListOf(),
		/**
		 * If `false`, the entity's regular nametag will not be rendered.
		 */
		var renderEntityName: Boolean = true,
	)
}