package me.nobaboy.nobaaddons.config.util

import dev.celestialfault.histoire.Histoire
import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.utils.ErrorManager
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.minecraft.util/*? if >=1.21.5 {*//*.path*//*?}*/.PathUtil
import java.nio.file.Path
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.io.path.nameWithoutExtension

@PublishedApi
internal val DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ROOT)

/**
 * Attempts to call [loader], logging an error and renaming [path] if an error is encountered.
 */
inline fun <R> safeLoad(path: Path, loader: () -> R): Result<R> = runCatching(loader).onFailure {
	ErrorManager.logError("Failed to load a config file", it)

	val date = DATE_FORMATTER.format(ZonedDateTime.now())
	val name = "${path.nameWithoutExtension}-${date}"
	val backup = PathUtil.getNextUniqueName(NobaAddons.CONFIG_DIR, name, ".json")

	if(path.toFile().renameTo(path.parent.resolve(backup).toFile())) {
		NobaAddons.LOGGER.warn("Moved config file to $backup")
	} else {
		NobaAddons.LOGGER.warn("Couldn't rename config file")
	}
}

/**
 * Attempts to load the associated [Histoire] instance, logging an error and renaming the file if it fails.
 */
fun Histoire.safeLoad() = safeLoad(file.toPath(), ::load)

/**
 * Attaches a [ClientLifecycleEvents] listener for when the client is stopping which calls [Histoire.save]
 */
fun Histoire.saveOnExit() {
	ClientLifecycleEvents.CLIENT_STOPPING.register {
		try {
			save()
		} catch(ex: Throwable) {
			// nothing we can do at this point other than just log an error and give up
			NobaAddons.LOGGER.error("Failed to save ${this::class.simpleName} before shutdown", ex)
		}
	}
}
