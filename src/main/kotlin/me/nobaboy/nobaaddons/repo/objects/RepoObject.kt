package me.nobaboy.nobaaddons.repo.objects

import kotlinx.serialization.KSerializer
import me.nobaboy.nobaaddons.repo.Repo
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