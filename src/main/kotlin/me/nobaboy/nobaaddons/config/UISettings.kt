package me.nobaboy.nobaaddons.config

import dev.celestialfault.celestialconfig.AbstractConfig
import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.ui.data.TextElement
import me.nobaboy.nobaaddons.utils.ErrorManager
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import java.io.IOException

object UISettings : AbstractConfig(NobaAddons.CONFIG_DIR.resolve("ui.json")) {
	val itemPickupLog by TextElement("itemPickupLog")

	fun init() {
		try {
			load()
		} catch(ex: IOException) {
			ErrorManager.logError("Failed to load UI settings", ex)
		}

		ClientLifecycleEvents.CLIENT_STOPPING.register {
			try {
				save()
			} catch(ex: IOException) {
				// Using ErrorManager here is largely pointless, as the chat won't exist to see the error message in,
				// so just print it directly to the logs.
				NobaAddons.LOGGER.error("Failed to save UI settings", ex)
			}
		}
	}
}