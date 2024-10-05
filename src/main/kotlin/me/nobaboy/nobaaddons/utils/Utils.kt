package me.nobaboy.nobaaddons.utils

import kotlinx.io.IOException
import me.nobaboy.nobaaddons.NobaAddons
import java.awt.Desktop
import java.net.URI

object Utils {
    val onHypixel: Boolean
        get() = NobaAddons.mc.networkHandler?.serverInfo?.address?.endsWith(".hypixel.net") == true

    // TODO: Move to separate util
    fun openBrowser(uri: String) {
        try {
            Desktop.getDesktop().browse(URI.create(uri))
        } catch (ex: IOException) {
            NobaAddons.LOGGER.error("Failed to open URL: $uri", ex)
        }
    }

    fun getPlayerName(): String? = NobaAddons.mc.player?.name?.string
}