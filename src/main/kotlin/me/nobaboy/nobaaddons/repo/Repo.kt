package me.nobaboy.nobaaddons.repo

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.JsonElement
import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.TextUtils.buildLiteral
import me.nobaboy.nobaaddons.utils.TextUtils.runCommand
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import me.nobaboy.nobaaddons.utils.tr
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.util.Formatting
import org.jetbrains.annotations.Blocking
import org.jetbrains.annotations.UnmodifiableView
import java.util.*

/**
 * Utility methods for interacting with the [RepoManager]
 */
object Repo {
	val JSON by NobaAddons::JSON

	private val updateFailed: Boolean by RepoManager::repoDownloadFailed
	private var sentRepoWarning = false

	val knownRegexKeys = mutableSetOf<String>()
	val knownStringKeys = mutableSetOf<String>()

	fun init() {
		RepoManager.add(RepoConstants.Regexes)
		RepoManager.add(RepoConstants.Strings)
		ClientTickEvents.END_CLIENT_TICK.register { onTick() }
	}

	private fun onTick() {
		if(MCUtils.player == null) return
		if(sentRepoWarning) return
		if(!updateFailed) return

		val command = buildLiteral("/noba repo update") { formatted(Formatting.AQUA).runCommand() }
		ChatUtils.addMessage(tr("nobaaddons.repo.updateFailed", "Failed to update mod data repository! Some features may not work properly until this is fixed; you may be able to resolve this issue with $command. If this issue persists, please join the Discord and ask for support."), color = Formatting.RED)
		sentRepoWarning = true
	}

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
		return RepoObject(path, serializer).also(RepoManager::add)
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
		return RepoObjectArray(path, serializer).also(RepoManager::add)
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
	fun <T : Any> createMapFromDirectory(path: String, serializer: KSerializer<T>): RepoObjectArray<T> {
		return RepoObjectArray(path, serializer).also(RepoManager::add)
	}

	/**
	 * Returns an unmodifiable view of all registered [IRepoObject] instances
	 */
	fun objects(): @UnmodifiableView Collection<IRepoObject> = Collections.unmodifiableCollection(RepoManager.objects)

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
	fun Regex.fromRepo(key: String) = RepoConstants.Entry(key, this, RepoConstants.Regexes).also { knownRegexKeys.add(key) }

	/**
	 * Creates a [RepoConstants.Entry] object supplying a string from the mod repository, falling
	 * back to [this] if none exists.
	 */
	fun String.fromRepo(key: String) = RepoConstants.Entry(key, this, RepoConstants.Strings).also { knownStringKeys.add(key) }

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