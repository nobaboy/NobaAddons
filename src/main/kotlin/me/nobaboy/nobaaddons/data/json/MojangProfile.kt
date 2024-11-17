package me.nobaboy.nobaaddons.data.json

data class MojangProfile(val id: String, val name: String, val properties: List<Properties>) {
	data class Properties(val name: String, val value: String, val signature: String?)
}