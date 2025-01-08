package me.nobaboy.nobaaddons.features.mining

import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI.inIsland
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.core.SkyBlockIsland
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import me.nobaboy.nobaaddons.utils.render.RenderUtils
import me.nobaboy.nobaaddons.utils.tr
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents

// TODO: Move into crystal hollows category later on, also a tracker would be nice
object WormAlert {
	private val config get() = NobaConfig.INSTANCE.mining.wormAlert
	private val enabled: Boolean get() = config.enabled && SkyBlockIsland.CRYSTAL_HOLLOWS.inIsland()

	fun init() {
		ClientReceiveMessageEvents.GAME.register { message, _ -> onChatMessage(message.string.cleanFormatting())}
	}

	private fun onChatMessage(message: String) {
		if(!enabled) return

		if(message == "You hear the sound of something approaching...") {
			RenderUtils.drawTitle(tr("nobaaddons.mining.wormAlert.spawned", "Worm Spawned!"), config.alertColor)
			config.notificationSound.play()
		}
	}
}