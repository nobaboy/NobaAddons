package me.nobaboy.nobaaddons.repo

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.mojang.util.InstantTypeAdapter
import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.repo.adapters.NobaVecAdapter
import me.nobaboy.nobaaddons.utils.NobaVec
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import net.minecraft.util.Util
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.RepositoryBuilder
import org.jetbrains.annotations.Blocking
import java.io.File
import java.io.FileReader
import java.time.Instant
import java.util.concurrent.CompletableFuture
import kotlin.jvm.java

object Repo {
	private val config get() = NobaConfigManager.config.repo

	val REPO_DIRECTORY: File = NobaAddons.CONFIG_DIR.resolve("repo").toFile()
	val GSON: Gson = GsonBuilder()
		.registerTypeAdapter(Instant::class.java, InstantTypeAdapter())
		.registerTypeAdapter(NobaVec::class.java, NobaVecAdapter())
		.create()

	@Volatile var announceRepoUpdate: Boolean = false
	@Volatile lateinit var commit: String
		private set

	private val git: Git = Git(RepositoryBuilder().setWorkTree(REPO_DIRECTORY).setMustExist(false).build())
	private var loaded = false
	private val objects: MutableList<IRepoObject> = mutableListOf()

	fun init() {
		register(RepoRegex)
		update()
	}

	fun update(): CompletableFuture<Void> =
		CompletableFuture.runAsync({
			runCatching {
				updateInternal()
			}.onFailure { NobaAddons.LOGGER.error("Failed to initialize repository", it) }
			reloadObjects()
		}, Util.getIoWorkerExecutor())

	@Blocking
	private fun updateInternal() {
		if(!REPO_DIRECTORY.exists()) {
			clone()
			return
		}

		// TODO properly update if config.uri is changed
		pull()
		if(git.repository.branch != config.branch) {
			git.checkout().setName(config.branch).call()
		}

		if(announceRepoUpdate) {
			ChatUtils.addMessage("Repository updated")
			announceRepoUpdate = false
		}
	}

	@Blocking
	private fun clone() {
		NobaAddons.LOGGER.debug("Cloning repository")
		Git.cloneRepository()
			.setDirectory(REPO_DIRECTORY)
			.setURI(config.uri)
			.setBranch(config.branch)
			.call()
		commit = git.repository.resolve("HEAD").name
		loaded = true
	}

	@Blocking
	private fun pull() {
		NobaAddons.LOGGER.debug("Pulling repository changes")
		git.pull().call()
		loaded = true
		commit = git.repository.resolve("HEAD").name
	}

	private fun reloadObjects() {
		NobaAddons.LOGGER.debug("Reloading repository objects")
		objects.forEach {
			println(it)
			runCatching { it.load() }.onFailure { NobaAddons.LOGGER.error("Repo object failed to reload", it) }
		}
	}

	fun register(obj: IRepoObject) {
		if(loaded) obj.load()
		objects.add(obj)
	}

	fun regex(key: String, fallback: Regex): RepoRegex.RegexEntry =
		RepoRegex.RegexEntry(key, fallback)

	fun regex(entries: Collection<RepoRegex.RegexEntry>): RepoRegex.RegexEntries =
		RepoRegex.RegexEntries(entries)

	fun Regex.fromRepo(key: String) = regex(key, this)

	fun <T> File.readJson(cls: Class<T>): T {
		return FileReader(this).use { GSON.fromJson(it, cls) }
	}
}