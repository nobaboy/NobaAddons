package nobaaddonsbuild

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.net.URI

// Code taken from SkyHanni (which is in turn taken from NotEnoughUpdates)
abstract class DownloadBackupRepo : DefaultTask() {
	@get:OutputDirectory
	abstract val outputDirectory: DirectoryProperty

	@get:Input
	abstract var branch: String

	@get:Internal
	val repoFile get() = outputDirectory.get().asFile.resolve("assets/nobaaddons/repo.zip")

	@TaskAction
	fun downloadRepo() {
		val downloadUrl = URI.create("https://github.com/nobaboy/NobaAddons-REPO/archive/refs/heads/$branch.zip").toURL()
		val file = repoFile
		file.parentFile.mkdirs()
		file.outputStream().use { out ->
			downloadUrl.openStream().use { inp ->
				inp.copyTo(out)
			}
		}
	}
}