package me.nobaboy.nobaaddons.repo

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.JsonElement
import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.repo.objects.IRepoObject
import me.nobaboy.nobaaddons.repo.objects.RepoConstants
import me.nobaboy.nobaaddons.repo.objects.RepoObject
import me.nobaboy.nobaaddons.repo.objects.RepoObjectArray
import me.nobaboy.nobaaddons.repo.objects.RepoObjectMap
import org.jetbrains.annotations.Blocking
import org.jetbrains.annotations.UnmodifiableView
import java.util.Collections

/**
 * Utility methods for interacting with the [RepoManager]
 */
object Repo {
	val JSON by NobaAddons::JSON

	val knownRegexKeys get() = RepoConstants.Regexes.entries.keys
	val knownStringKeys get() = RepoConstants.Strings.entries.keys

	/**
	 * Returns an unmodifiable view of all registered [IRepoObject] instances
	 */
	val objects: @UnmodifiableView Collection<IRepoObject>
		get() = Collections.unmodifiableCollection(RepoManager.objects)

	/**
	 * Creates a new [RepoObject] supplying a single instance of [T] loaded from the mod's repository
	 *
	 * The supplied value may be null if the repository failed to load.
	 *
	 * ## Example
	 *
	 * ```kt
	 * @Serializable
	 * data class DataClass(val string: String, val number: Int)
	 *
	 * val DATA by Repo.create("feature.json", DataClass.serializer())
	 * ```
	 */
	fun <T : Any> create(path: String, serializer: KSerializer<T>): RepoObject<T> {
		return RepoObject(path, serializer).also(RepoManager::performInitialLoad)
	}

	/**
	 * Creates a new [RepoObjectArray] supplying a list of [T] instances loaded from a JSON file containing an array
	 * of objects from the mod's repository
	 *
	 * The supplied list may be empty if the repository failed to load.
	 *
	 * ## Example
	 *
	 * ```kt
	 * @Serializable
	 * data class DataClass(val string: String, val number: Int)
	 *
	 * val DATA by Repo.createArray("features.json", DataClass.serializer())
	 * ```
	 */
	fun <T : Any> createList(path: String, serializer: KSerializer<T>): RepoObjectArray<T> {
		return RepoObjectArray(path, serializer).also(RepoManager::performInitialLoad)
	}

	/**
	 * Creates a new [RepoObjectMap] supplying a map of [String] file names to instances of [T]
	 *
	 * The supplied map may be empty if the repository failed to load.
	 *
	 * ## Example
	 *
	 * ```kt
	 * @Serializable
	 * data class DataClass(val string: String, val number: Int)
	 *
	 * val DATA by Repo.createMapFromDirectory("feature/", DataClass.serializer())
	 * ```
	 */
	fun <T : Any> createMapFromDirectory(path: String, serializer: KSerializer<T>): RepoObjectMap<T> {
		return RepoObjectMap(path, serializer).also(RepoManager::performInitialLoad)
	}

	/**
	 * Creates a [RepoConstants.Entries] object supplying a list of mapped values from the provided
	 * [RepoConstants.Entry] objects.
	 */
	fun <T> list(vararg entries: RepoConstants.Entry<T>): RepoConstants.Entries<T> =
		RepoConstants.Entries(entries.toList())

	/**
	 * Creates a [RepoConstants.Entry] object supplying a regex pattern from the mod repository,
	 * falling back to [this] if none exists.
	 */
	fun Regex.fromRepo(key: String) = RepoConstants.Entry(key, this, RepoConstants.Regexes)

	/**
	 * Creates a [RepoConstants.Entry] object supplying a string from the mod repository, falling
	 * back to [this] if none exists.
	 */
	fun String.fromRepo(key: String) = RepoConstants.Entry(key, this, RepoConstants.Strings)

	/**
	 * Creates a [RepoConstants.Entry] object supplying a skull texture from the mod repository,
	 * falling back to [this] if none exists.
	 */
	fun String.skullFromRepo(key: String) = RepoConstants.Entry(key, this, RepoConstants.SkullTextures)

	/**
	 * Reads the file located at the provided [path] relative to the repository directory root,
	 * and returns its contents as a [String]
	 */
	@Blocking
	fun readAsString(path: String): String = RepoManager.REPO_DIRECTORY.resolve(path).readText()

	/**
	 * Reads the file located at the provided [path] relative to the repository directory root,
	 * and returns its contents as a [JsonElement]
	 */
	@Blocking
	fun readAsJson(path: String): JsonElement = JSON.parseToJsonElement(readAsString(path))
}