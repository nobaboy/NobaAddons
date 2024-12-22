package me.nobaboy.nobaaddons.repo

import kotlinx.serialization.KSerializer
import kotlin.reflect.KProperty

class RepoObject<T : Any>(private val path: String, private val serializer: KSerializer<T>) : IRepoObject {
	@Volatile private var instance: T? = null

	@Suppress("unused")
	operator fun getValue(instance: Any, property: KProperty<*>): T? = this.instance

	override fun load() {
		instance = Repo.JSON.decodeFromString(serializer, Repo.readAsString(path))
	}

	override fun toString(): String = "RepoObject(value=$instance, repoPath=$path)"
}