package me.nobaboy.nobaaddons.config

import dev.celestialfault.celestialconfig.AbstractConfig
import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.config.NobaConfigUtils.safeLoad
import me.nobaboy.nobaaddons.ui.data.TextElement
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import java.io.IOException

object UISettings : AbstractConfig(NobaAddons.CONFIG_DIR.resolve("ui.json")) {
	val itemPickupLog by TextElement("itemPickupLog")

	fun init() {
		safeLoad()
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