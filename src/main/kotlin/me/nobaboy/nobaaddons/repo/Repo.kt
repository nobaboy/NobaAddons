package me.nobaboy.nobaaddons.repo

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.mojang.util.InstantTypeAdapter
import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.repo.adapters.NobaVecAdapter
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.NobaVec
import me.nobaboy.nobaaddons.utils.TextUtils.formatted
import me.nobaboy.nobaaddons.utils.TextUtils.runCommand
import me.nobaboy.nobaaddons.utils.TextUtils.translatable
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import me.nobaboy.nobaaddons.utils.properties.CacheFor
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.minecraft.util.Formatting
import net.minecraft.util.Util
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.RepositoryBuilder
import org.eclipse.jgit.transport.URIish
import org.jetbrains.annotations.Blocking
import org.jetbrains.annotations.UnmodifiableView
import java.io.File
import java.io.FileReader
import java.nio.file.Path
import java.time.Instant
import java.util.Collections
import java.util.concurrent.CompletableFuture
import kotlin.jvm.java
import kotlin.time.Duration.Companion.seconds

/*
TODO: Bundle a copy of the repo in the mod jar for users that can't clone it? Similar to how NEU does it
*/

object Repo {
	private val config get() = NobaConfigManager.config.repo

	val REPO_DIRECTORY: File = run {
		System.getProperty("nobaaddons.repoDir")?.let { return@run Path.of(it).toFile() }
		NobaAddons.CONFIG_DIR.resolve("repo").toFile()
	}
	private val GIT_DIRECTORY: File = REPO_DIRECTORY.resolve(".git")

	val GSON: Gson = GsonBuilder()
		.registerTypeAdapter(Instant::class.java, InstantTypeAdapter())
		.registerTypeAdapter(NobaVec::class.java, NobaVecAdapter())
		.create()

	var announceRepoUpdate: Boolean = false
	var updateFailed: Boolean = false
		private set
	// we want this to be immediately visible to all threads, while the others
	// can be left to be eventually consistent.
	@Volatile var loaded: Boolean = false
		private set

	// note that while these *are* blocking, I'm not too worried about these, given that they don't implicitly make any
	// network requests of any kind.
	val isDirty: Boolean by CacheFor(60.seconds) { if(GIT_DIRECTORY.exists()) !git.status().call().isClean else false }
	val commit: String? by CacheFor(60.seconds) { if(GIT_DIRECTORY.exists()) git.repository.resolve("HEAD").name else null }

	private val git: Git = Git(RepositoryBuilder().setWorkTree(REPO_DIRECTORY).setMustExist(false).build())
	private val objects: MutableSet<IRepoObject> = mutableSetOf()
	private var sentRepoWarning = false

	val knownRegexKeys = mutableSetOf<String>()
	val knownStringKeys = mutableSetOf<String>()

	fun init() {
		register(RepoValues.Regexes)
		register(RepoValues.Strings)
		ClientTickEvents.END_CLIENT_TICK.register { onTick() }
		ClientLifecycleEvents.CLIENT_STARTED.register { update() }
		ClientLifecycleEvents.CLIENT_STOPPING.register { git.repository.close() }
		ClientPlayConnectionEvents.DISCONNECT.register { _, _ -> sentRepoWarning = false }
	}

	private fun onTick() {
		if(MCUtils.player == null) return
		if(sentRepoWarning) return
		if(!updateFailed) return

		ChatUtils.addMessage(color = Formatting.RED) {
			translatable(
				"nobaaddons.repo.updateFailed",
				"/noba repo update".formatted(Formatting.AQUA).runCommand()
			)
		}
		sentRepoWarning = true
	}

	/**
	 * Schedules an update for the repository. The returned [CompletableFuture] may be used to determine
	 * when the process finishes, but does not indicate whether it completed successfully or not.
	 */
	fun update(): CompletableFuture<Void> =
		CompletableFuture.runAsync(this::updateInternal, Util.getIoWorkerExecutor())

	@Blocking
	private fun updateInternal() {
		runCatching {
			initOrPullRepo()
		}.onFailure {
			sentRepoWarning = false
			updateFailed = true
			NobaAddons.LOGGER.error("Failed to update repository", it)
		}.onSuccess { updateFailed = false }

		if(REPO_DIRECTORY.exists()) {
			reloadObjects()
		}

		if(announceRepoUpdate) {
			ChatUtils.addMessage { translatable("nobaaddons.repo.updated") }
			announceRepoUpdate = false
		}
	}

	@Blocking
	private fun initOrPullRepo() {
		if(!REPO_DIRECTORY.exists()) {
			clone()
			return
		}
		if(!config.autoUpdate) return
		pull()
	}

	@Blocking
	private fun clone() {
		NobaAddons.LOGGER.debug("Cloning repository")
		Git.cloneRepository()
			.setDirectory(REPO_DIRECTORY)
			.setURI(config.uri)
			.setBranch(config.branch)
			.call()
			.close()
	}

	@Blocking
	private fun pull() {
		NobaAddons.LOGGER.debug("Pulling repository changes")

		// why is kotlin transforming 'getURIs()' to 'urIs'?
		val currentUri = git.remoteList().call().first { it.name == "origin" }.urIs.first().toPrivateString()
		if(currentUri != config.uri) {
			NobaAddons.LOGGER.debug("Updating repository remote URI")
			git.remoteSetUrl().setRemoteName("origin").setRemoteUri(URIish(config.uri)).call()
		}
		if(git.repository.branch != config.branch) {
			NobaAddons.LOGGER.debug("Checking out {}", config.branch)
			git.checkout().setName(config.branch).call()
		}

		git.pull().call()
	}

	@Blocking
	private fun reloadObjects() {
		NobaAddons.LOGGER.debug("Reloading repository objects")
		loaded = true
		objects.forEach {
			runCatching { it.load() }.onFailure { NobaAddons.LOGGER.error("Repo object failed to reload", it) }
		}
	}

	/**
	 * Register the supplied [IRepoObject], and loads it if the repository has already been [loaded].
	 */
	fun register(obj: IRepoObject) {
		if(loaded) obj.load()
		objects.add(obj)
	}

	/**
	 * Returns an unmodifiable view of all registered [IRepoObject] instances
	 */
	fun objects(): @UnmodifiableView Collection<IRepoObject> = Collections.unmodifiableCollection(objects)

	/**
	 * Creates a [RepoValues.Entries] object supplying a list of mapped values from the provided
	 * [RepoValues.Entry] objects.
	 */
	fun <T> list(vararg entries: RepoValues.Entry<T>): RepoValues.Entries<T> =
		RepoValues.Entries(entries.toList())

	/**
	 * Creates a [RepoValues.Entry] object supplying a regex pattern from the mod repository,
	 * falling back to [this] if none exists.
	 */
	fun Regex.fromRepo(key: String) = RepoValues.Entry(key, this, RepoValues.Regexes).also { knownRegexKeys.add(key) }

	/**
	 * Creates a [RepoValues.Entry] object supplying a string from the mod repository, falling
	 * back to [this] if none exists.
	 */
	fun String.fromRepo(key: String) = RepoValues.Entry(key, this, RepoValues.Strings).also { knownStringKeys.add(key) }

	/**
	 * Loads [this] using GSON, returning a built instance of the provided class.
	 */
	fun <T> File.readJson(cls: Class<T>): T {
		return FileReader(this).use { GSON.fromJson(it, cls) }
	}
}