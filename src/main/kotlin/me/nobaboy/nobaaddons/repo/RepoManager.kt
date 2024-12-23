package me.nobaboy.nobaaddons.repo

import io.ktor.client.request.get
import io.ktor.client.statement.readRawBytes
import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.data.PersistentCache
import me.nobaboy.nobaaddons.repo.data.GithubCommitResponse
import me.nobaboy.nobaaddons.repo.objects.IRepoObject
import me.nobaboy.nobaaddons.utils.HTTPUtils
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import me.nobaboy.nobaaddons.utils.tr
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.util.Formatting
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import kotlin.io.path.pathString
import kotlin.io.path.writeBytes

/**
 * The repository manager which controls downloading the mod's data repository.
 */
object RepoManager {
	private val LOCK = Any()
	private val config get() = NobaConfigManager.config.repo

	private val client by HTTPUtils::client
	internal val objects = mutableSetOf<IRepoObject>()
	val REPO_DIRECTORY: File = run {
		System.getProperty("nobaaddons.repoDir")?.let { return@run Path.of(it).toFile() }
		NobaAddons.CONFIG_DIR.resolve("repo").toFile()
	}

	private val TEMPORARY_DIRECTORY = Files.createTempDirectory("nobaaddons")
	private val ZIP_PATH get() = TEMPORARY_DIRECTORY.resolve("repo.zip").apply { toFile().createNewFile() }

	/**
	 * Set to `true` when the repository has first been loaded.
	 */
	@Volatile var loaded: Boolean = false
		private set

	/**
	 * If `true`, downloading the repository has failed; we may be using the backup repository
	 * (this can be checked through [commit] being `backup-repo`)
	 */
	var repoDownloadFailed: Boolean = false
		private set

	private var sendChatMessage: Boolean = false
	var commit: String? by PersistentCache::repoCommit

	private val username: String get() = config.username
	private val repository: String get() = config.repository
	private val branch: String get() = config.branch

	val githubUrl: String get() = "https://github.com/$username/$repository"
	val downloadUrl: String get() = "https://github.com/$username/$repository/archive/refs/heads/$branch.zip"
	val commitApiUrl: String get() = "https://api.github.com/repos/$username/$repository/commits/$branch"
	fun commitUrl(commit: String): String = "$githubUrl/commit/$commit"

	fun init() {
		NobaAddons.runAsync { update() }
		ClientLifecycleEvents.CLIENT_STOPPING.register { cleanupTemporaryDirectory() }
	}

	private suspend fun getLatestCommit(): String =
		HTTPUtils.fetchJson(commitApiUrl, GithubCommitResponse.serializer()).await().sha

	suspend fun update(sendMessages: Boolean = false) {
		this.sendChatMessage = sendMessages
		download()
		reload()
		this.sendChatMessage = false
	}

	private suspend fun download() {
		if(!config.autoUpdate && REPO_DIRECTORY.exists()) {
			if(sendChatMessage) {
				ChatUtils.addMessage(tr("nobaaddons.repo.autoUpdateOff", "Auto updating has been disabled, not updating repository"))
			}
			loaded = true
			return
		}

		val latestCommit: String
		try {
			latestCommit = getLatestCommit()
		} catch(e: Exception) {
			NobaAddons.LOGGER.error("Failed to get latest repo commit", e)
			return
		}

		if(commit == latestCommit && REPO_DIRECTORY.exists()) {
			loaded = true
			if(sendChatMessage) {
				ChatUtils.addMessage(tr("nobaaddons.repo.alreadyUpdated", "Repository is already up to date"))
			}
			return
		}

		val repoZip = ZIP_PATH
		try {
			val url = downloadUrl
			val zip = client.get(url)
			repoZip.writeBytes(zip.readRawBytes())
		} catch(e: Exception) {
			NobaAddons.LOGGER.error("Failed to download repo", e)
			if(!REPO_DIRECTORY.exists()) {
				switchToBackupRepo()
			}
			if(sendChatMessage) {
				ChatUtils.addMessage(tr("nobaaddons.repo.downloadFailed", "Failed to download repository"), color = Formatting.RED)
			}
			repoDownloadFailed = true
			return
		}

		loaded = false
		REPO_DIRECTORY.mkdirs()
		try {
			RepoUtils.unzipIgnoreFirstFolder(repoZip.pathString, REPO_DIRECTORY.absolutePath)
		} catch(e: Exception) {
			NobaAddons.LOGGER.error("Failed to unzip repository", e)
			if(sendChatMessage) {
				ChatUtils.addMessage(tr("nobaaddons.repo.unpackFailed", "Failed to unpack downloaded repository"), color = Formatting.RED)
			}
			repoDownloadFailed = true
			switchToBackupRepo()
			return
		}

		commit = latestCommit
		if(sendChatMessage) {
			ChatUtils.addMessage(tr("nobaaddons.repo.updated", "Updated repository"))
		}
		loaded = true
		repoDownloadFailed = false
	}

	fun reload() {
		synchronized(LOCK) {
			objects.forEach {
				runCatching { it.load() }.onFailure { NobaAddons.LOGGER.error("Repo object failed to load", it) }
			}
		}
		if(FabricLoader.getInstance().isDevelopmentEnvironment && sendChatMessage) {
			ChatUtils.addMessage("Repo objects reloaded")
		}
	}

	fun add(obj: IRepoObject) {
		synchronized(LOCK) {
			if(loaded) obj.load()
			objects.add(obj)
		}
	}

	// Code taken from NotEnoughUpdates
	private fun switchToBackupRepo() {
		NobaAddons.LOGGER.warn("Attempting to switch to backup repo")

		try {
			REPO_DIRECTORY.mkdirs()
			val destinationPath = ZIP_PATH

			val inputStream = RepoManager::class.java.classLoader.getResourceAsStream("assets/${NobaAddons.MOD_ID}/repo.zip")
				?: throw IOException("Failed to find backup repo")

			Files.copy(inputStream, destinationPath, StandardCopyOption.REPLACE_EXISTING)
			RepoUtils.unzipIgnoreFirstFolder(destinationPath.toAbsolutePath().toString(), REPO_DIRECTORY.absolutePath)
			commit = "backup-repo"

			loaded = true
			NobaAddons.LOGGER.warn("Successfully switched to backup repo")
		} catch(e: Exception) {
			NobaAddons.LOGGER.error("Failed to switch to backup repo", e)
		}
	}

	private fun cleanupTemporaryDirectory() {
		try {
			RepoUtils.recursiveDelete(TEMPORARY_DIRECTORY)
		} catch(e: Exception) {
			NobaAddons.LOGGER.warn("Failed to delete temporary directory", e)
		}
	}
}