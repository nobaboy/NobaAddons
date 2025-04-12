package me.nobaboy.nobaaddons.repo

import kotlinx.serialization.serializer
import me.nobaboy.nobaaddons.NobaAddons
import org.intellij.lang.annotations.Language
import org.jetbrains.annotations.UnmodifiableView
import java.util.Collections

/**
 * Utility methods for interacting with the [RepoManager]
 */
object Repo {
	val JSON by NobaAddons::JSON

	private val regexHandleProvider = RepoSourceHandleProvider<Regex>(RepoSource.Constants.REGEX)
	private val stringHandleProvider = RepoSourceHandleProvider<String>(RepoSource.Constants.STRINGS)
	private val skullTextureHandleProvider = RepoSourceHandleProvider<String>(RepoSource.Constants.SKULLS)

	val usedRegexKeys: Set<String> = Collections.unmodifiableSet(regexHandleProvider.handles.keys)
	val usedStringKeys: Set<String> = Collections.unmodifiableSet(stringHandleProvider.handles.keys)

	/**
	 * Returns an unmodifiable view of all registered [IRepoSource] instances
	 */
	val objects: @UnmodifiableView Collection<IRepoSource> = Collections.unmodifiableCollection(RepoManager.objects)

	internal fun logMismatchedHandles() {
		println(regexHandleProvider.handles.filterValues(RepoHandle<*>::overridden))
		println(stringHandleProvider.handles.filterValues(RepoHandle<*>::overridden))
		println(skullTextureHandleProvider.handles.filterValues(RepoHandle<*>::overridden))
	}

	/**
	 * Creates a new [RepoSource] supplying a single instance of [T] loaded from the mod's repository
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
	inline fun <reified T : Any> create(path: String): RepoSource<T> =
		RepoSource.Single(path, serializer<T>()).also(RepoManager::performInitialLoad)

	/**
	 * Creates a new [RepoSource] supplying a list of [T] instances loaded from a JSON file containing an array
	 * of objects from the mod's repository
	 *
	 * The supplied list may be null if the repository failed to load.
	 *
	 * ## Example
	 *
	 * ```kt
	 * @Serializable
	 * data class DataClass(val string: String, val number: Int)
	 *
	 * val DATA by Repo.createList<DataClass>("features.json")
	 * ```
	 */
	inline fun <reified T : Any> createList(path: String): RepoSource.List<T> =
		RepoSource.List(path, serializer<T>()).also(RepoManager::performInitialLoad)

	/**
	 * Creates a [RepoHandleList] object supplying a list of mapped values from the provided
	 * [RepoHandle] objects.
	 */
	fun <T> list(vararg entries: RepoHandle<T>) = RepoHandleList(entries.toList())

	/**
	 * Creates a [RepoHandle] object supplying a string from the mod repository, falling
	 * back to [fallback] if none exists.
	 */
	fun string(key: String, fallback: String) = stringHandleProvider.get(key, fallback)

	/**
	 * Creates a [RepoHandle] object supplying a regex pattern from the mod repository,
	 * falling back to [fallback] if none exists.
	 */
	fun regex(key: String, @Language("RegExp") fallback: String) = regexHandleProvider.get(key, Regex(fallback))

	/**
	 * Creates a [RepoHandle] object supplying a regex pattern from the mod repository,
	 * falling back to [fallback] if none exists.
	 */
	fun skull(key: String, @Language("Base64") fallback: String) = skullTextureHandleProvider.get(key, fallback)
}