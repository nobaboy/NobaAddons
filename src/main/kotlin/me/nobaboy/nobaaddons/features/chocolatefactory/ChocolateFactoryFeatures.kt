package me.nobaboy.nobaaddons.features.chocolatefactory

import me.nobaboy.nobaaddons.api.skyblock.PetAPI
import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI
import me.nobaboy.nobaaddons.api.skyblock.events.hoppity.HoppityAPI
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.core.Rarity
import me.nobaboy.nobaaddons.events.PacketEvents
import me.nobaboy.nobaaddons.events.SendMessageEvents
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.TextUtils.hoverText
import me.nobaboy.nobaaddons.utils.TextUtils.yellow
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import me.nobaboy.nobaaddons.utils.render.RenderUtils
import me.nobaboy.nobaaddons.utils.tr
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket
import net.minecraft.util.Formatting

object ChocolateFactoryFeatures {
	private val config get() = NobaConfig.INSTANCE.events.hoppity
	private val enabled: Boolean get() = config.requireMythicRabbit && SkyBlockAPI.inSkyBlock

	private val hasMythicRabbitSpawned: Boolean get() =
		PetAPI.currentPet?.id == "RABBIT" && PetAPI.currentPet?.rarity == Rarity.MYTHIC

	fun init() {
		PacketEvents.SEND.register(this::onSendPacket)
		SendMessageEvents.SEND_COMMAND.register(this::onSendCommand)
	}

	private fun onSendPacket(event: PacketEvents.Send) {
		if(!HoppityAPI.isSpring || !HoppityAPI.inRelevantIsland) return
		if(!enabled) return

		val packet = event.packet
		if(packet !is PlayerInteractItemC2SPacket && packet !is PlayerInteractBlockC2SPacket) return

		if(!HoppityAPI.hasLocatorInHand) return
		if(hasMythicRabbitSpawned) return

		RenderUtils.drawTitle(tr("nobaaddons.chocolateFactory.spawnMythicRabbit", "Spawn Mythic Rabbit!"), NobaColor.RED)
	}

	private fun onSendCommand(event: SendMessageEvents.SendCommand) {
		if(!enabled) return

		val command = event.command.split(" ")[0].lowercase()
		if(command != "cf" && command != "chocolatefactory") return
		if(hasMythicRabbitSpawned) return

		event.cancel()
		ChatUtils.addMessageWithClickAction(
			tr("nobaaddons.chocolateFactory.commandNoMythicRabbit", "Blocked opening Chocolate Factory without a Mythic Rabbit pet!")
				.hoverText(tr("nobaaddons.chocolateFactory.clickPetsMenu", "Click to open the Pets menu").yellow()),
			color = Formatting.RED,
		) { ChatUtils.queueCommand("pets") }
	}
}