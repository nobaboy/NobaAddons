package me.nobaboy.nobaaddons.repo

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.JsonArray
import kotlin.reflect.KProperty

class RepoObjectArray<T : Any>(val path: String, private val serializer: KSerializer<T>) : IRepoObject {
	@Volatile private var instances: List<T> = emptyList()

	@Suppress("unused")
	operator fun getValue(instance: Any, property: KProperty<*>): List<T> = instances

	override fun load() {
		val array = Repo.readAsJson(path)
		check(array is JsonArray)
		instances = array.toList().map { Repo.JSON.decodeFromJsonElement(serializer, it) }
	}

	override fun toString(): String = "RepoObjectArray(value=$instances, repoPath=$path)"
}