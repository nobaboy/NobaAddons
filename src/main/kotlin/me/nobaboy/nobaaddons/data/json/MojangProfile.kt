package me.nobaboy.nobaaddons.data.json

import kotlinx.serialization.Serializable

@Serializable
data class MojangProfile(val id: String, val name: String, val properties: List<Properties>) {
	@Serializable
	data class Properties(val name: String, val value: String, val signature: String? = null)
}