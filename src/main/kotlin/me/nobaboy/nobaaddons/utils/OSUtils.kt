package me.nobaboy.nobaaddons.utils

import me.nobaboy.nobaaddons.NobaAddons
import java.awt.Desktop
import java.io.IOException
import java.net.URI

object OSUtils {
	fun openBrowser(url: String) {
		val isSupported = Desktop.isDesktopSupported()
		val canBrowse = Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)
		if (isSupported && canBrowse) {
			try {
				Desktop.getDesktop().browse(URI.create(url))
			} catch (ex: IOException) {
				NobaAddons.LOGGER.error("Failed to browse link: $url", ex)
			}
		} else {
			NobaAddons.LOGGER.error("This device does not support browsing.")
		}
	}
}