package me.nobaboy.nobaaddons.repo

import kotlinx.serialization.KSerializer
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.nameWithoutExtension
import kotlin.io.path.readText
import kotlin.reflect.KProperty

class RepoObjectMap<T>(private val path: String, private val cls: Class<T>, private val serializer: KSerializer<T>) : IRepoObject {
	@Volatile private var instances: Map<String, T> = emptyMap()

	@Suppress("unused")
	operator fun getValue(instance: Any, property: KProperty<*>): Map<String, T> = instances

	override fun load() {
		val files = Repo.REPO_DIRECTORY.toPath().resolve(this.path).listDirectoryEntries("*.json")
		instances = files.associate { it.nameWithoutExtension to Repo.JSON.decodeFromString(serializer, it.readText()) }
	}

	override fun toString(): String = "RepoObjectDirectory(value=$instances, repoPath=$path, class=$cls)"
}