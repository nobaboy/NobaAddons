package me.nobaboy.nobaaddons.repo

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import me.nobaboy.nobaaddons.events.impl.RepoReloadEvent
import me.nobaboy.nobaaddons.utils.serializers.RegexKSerializer
import kotlin.reflect.KProperty

/**
 * Simple repo source implementation
 */
sealed class RepoSource<T>(val file: String, val serializer: KSerializer<T>) : IRepoSource {
	init {
		// TODO rework this
		RepoReloadEvent.EVENT.register { this.load() }
	}

	@Volatile var value: T? = null
		protected set

	override fun load() {
		value = Repo.JSON.decodeFromString(serializer, RepoManager.REPO_DIRECTORY.resolve(file).readText())
	}

	@Suppress("unused")
	operator fun getValue(instance: Any?, property: KProperty<*>): T? = value

	override fun toString(): String = "RepoSource(file=$file, serializer=$serializer)"

	/**
	 * Basic implementation providing a [T] from a repo file
	 */
	class Single<T>(file: String, serializer: KSerializer<T>) : RepoSource<T>(file, serializer)

	/**
	 * Implementation supplying a map of [T] values mapped to string keys
	 */
	class Map<T>(file: String, serializer: KSerializer<T>) : RepoSource<kotlin.collections.Map<String, T>>(file, MapSerializer(String.serializer(), serializer)) {
		operator fun get(key: String): T? = value?.get(key)
	}

	/**
	 * Implementation supplying a list of [T] values
	 */
	class List<T>(file: String, serializer: KSerializer<T>) : RepoSource<kotlin.collections.List<T>>(file, ListSerializer(serializer)) {
		operator fun get(index: Int): T? = value?.get(index)
	}

	object Constants {
		val REGEX = Map("data/regexes.json", RegexKSerializer)
		val STRINGS = Map("data/strings.json", String.serializer())
		val SKULLS = Map("data/skull_textures.json", String.serializer())
	}
}