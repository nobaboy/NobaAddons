package me.nobaboy.nobaaddons.core.attributes

import kotlinx.serialization.Serializable
import me.nobaboy.nobaaddons.repo.Repo

@Serializable
data class Attribute(
	val id: String,
	val name: String,
	val abbreviation: String
) {
	companion object {
		val ATTRIBUTES by Repo.createList<Attribute>("item_modifiers/attributes.json")

		fun getById(id: String): Attribute? = ATTRIBUTES?.firstOrNull { it.id == id }
	}
}
