package me.nobaboy.nobaaddons.repo

import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.core.PersistentCache
import me.nobaboy.nobaaddons.events.impl.RepoReloadEvent
import me.nobaboy.nobaaddons.repo.data.GithubCommitResponse
import me.nobaboy.nobaaddons.repo.objects.IRepoObject
import me.nobaboy.nobaaddons.utils.ErrorManager
import me.nobaboy.nobaaddons.utils.FileUtils
import me.nobaboy.nobaaddons.utils.HTTPUtils
import me.nobaboy.nobaaddons.utils.HTTPUtils.get
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import me.nobaboy.nobaaddons.utils.tr
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.net.http.HttpResponse
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import kotlin.io.path.pathString
import kotlin.random.Random

/**
 * The repository manager which controls downloading the mod's data repository.
 */
object RepoManager {
	private val LOCK = Any()
	private val config get() = NobaConfig.INSTANCE.repo

	private val client by HTTPUtils::client
	internal val objects = mutableSetOf<IRepoObject>()
	val REPO_DIRECTORY: File = run {
		System.getProperty("nobaaddons.repoDir")?.let { return@run Path.of(it).toFile() }
		NobaAddons.CONFIG_DIR.resolve("repo").toFile()
	}

	var commit: String? by PersistentCache::repoCommit
		private set

	private val username: String get() = config.username
	private val repository: String get() = config.repository
	private val branch: String get() = config.branch

	val githubUrl: String get() = "https://github.com/$username/$repository"
	val downloadUrl: String get() = "https://github.com/$username/$repository/archive/refs/heads/$branch.zip"
	val commitApiUrl: String get() = "https://api.github.com/repos/$username/$repository/commits/$branch"
	fun commitUrl(commit: String): String = "$githubUrl/commit/$commit"

	fun init() {
		NobaAddons.runAsync { update() }
	}

	private suspend fun getLatestCommit(): String =
		HTTPUtils.fetchJson<GithubCommitResponse>(commitApiUrl).await().sha

	suspend fun update(sendMessages: Boolean = false, force: Boolean = false) {
		if(!config.autoUpdate && REPO_DIRECTORY.exists()) {
			if(sendMessages) {
				ChatUtils.addMessage(tr("nobaaddons.repo.autoUpdateOff", "Auto updating has been disabled, not updating repository"))
			}
			RepoReloadEvent.invoke()
			return
		}

		val latestCommit: String = try {
			getLatestCommit()
		} catch(e: Exception) {
			ErrorManager.logError("Failed to get latest repo commit", e, ignorePreviousErrors = true)
			if(!REPO_DIRECTORY.exists()) switchToBackupRepo() else RepoReloadEvent.invoke()
			return
		}

		if(commit == latestCommit && REPO_DIRECTORY.exists() && !force) {
			if(sendMessages) {
				ChatUtils.addMessage(tr("nobaaddons.repo.alreadyUpdated", "Repository is already up to date"))
			}
			RepoReloadEvent.invoke()
			return
		}

		FileUtils.withTempFile("nobaaddons", "repo_${Random.nextInt(0, 10000)}.zip") { repoZip ->
			try {
				val zip = client.get(downloadUrl, HttpResponse.BodyHandlers.ofByteArray())
				repoZip.writeBytes(zip.body())
			} catch(e: Exception) {
				ErrorManager.logError("Repository failed to download", e, ignorePreviousErrors = true)
				if(!REPO_DIRECTORY.exists()) switchToBackupRepo()
				return
			}

			try {
				REPO_DIRECTORY.mkdirs()
				RepoUtils.unzipIgnoreFirstFolder(repoZip.toPath().pathString, REPO_DIRECTORY.absolutePath)
			} catch(e: Exception) {
				ErrorManager.logError("Failed to unzip downloaded repository", e, ignorePreviousErrors = true)
				// always attempt to switch to the backup repo at this point, as it's possible that the
				// repo is in a broken state
				switchToBackupRepo()
				// backup repo will invoke RepoReloadEvent for us here
				return
			}
		}

		commit = latestCommit
		if(sendMessages) {
			ChatUtils.addMessage(tr("nobaaddons.repo.updated", "Updated repository"))
		}
		RepoReloadEvent.invoke()
	}

	fun performInitialLoad(obj: IRepoObject) {
		synchronized(LOCK) {
			try {
				if(REPO_DIRECTORY.exists()) obj.load()
			} catch(ex: FileNotFoundException) {
				NobaAddons.LOGGER.warn("Repo object failed to load missing file: {}", ex.message)
			} catch(ex: Throwable) {
				ErrorManager.logError("Repo object failed initial load", ex, "Repo object" to obj)
			}
			objects.add(obj)
		}
	}

	// Code taken from NotEnoughUpdates
	private fun switchToBackupRepo() {
		NobaAddons.LOGGER.warn("Attempting to switch to backup repo")

		try {
			REPO_DIRECTORY.mkdirs()

			FileUtils.withTempFile("nobaaddons", "backup-repo.zip") {
				val inputStream = RepoManager::class.java.classLoader.getResourceAsStream("assets/${NobaAddons.MOD_ID}/repo.zip")
					?: throw IOException("Failed to find backup repo")

				Files.copy(inputStream, it.toPath(), StandardCopyOption.REPLACE_EXISTING)
				RepoUtils.unzipIgnoreFirstFolder(it.toPath().toAbsolutePath().toString(), REPO_DIRECTORY.absolutePath)
			}

			commit = "backup-repo"

			NobaAddons.LOGGER.warn("Successfully switched to backup repo")
			RepoReloadEvent.invoke()
		} catch(e: Exception) {
			ErrorManager.logError("Failed to switch to backup repo", e, ignorePreviousErrors = true)
		}
	}

	private fun RepoReloadEvent.Companion.invoke() {
		synchronized(LOCK) { EVENT.invoke(RepoReloadEvent()) }
	}
}