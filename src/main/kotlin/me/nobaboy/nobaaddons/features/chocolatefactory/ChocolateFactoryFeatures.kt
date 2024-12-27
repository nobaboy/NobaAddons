package me.nobaboy.nobaaddons.features.chocolatefactory

import me.nobaboy.nobaaddons.api.skyblock.PetAPI
import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.core.Rarity
import me.nobaboy.nobaaddons.events.PacketEvents
import me.nobaboy.nobaaddons.events.SendMessageEvents
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.TextUtils.hoverText
import me.nobaboy.nobaaddons.utils.TextUtils.yellow
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import me.nobaboy.nobaaddons.utils.items.ItemUtils.getSkyBlockItem
import me.nobaboy.nobaaddons.utils.render.RenderUtils
import me.nobaboy.nobaaddons.utils.tr
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket
import net.minecraft.util.Formatting

object ChocolateFactoryFeatures {
	val config get() = NobaConfigManager.config.events.chocolateFactory

	fun init() {
		SendMessageEvents.SEND_COMMAND.register(this::onSendCommand)
		PacketEvents.SEND.register(this::onSendPacket)
	}

	private fun onSendCommand(event: SendMessageEvents.SendCommand) {
		if(!config.requireMythicRabbit) return
		val command = event.command.split(" ")[0].lowercase()
		if(command != "cf" && command != "chocolatefactory") return
		if(!hasMythicRabbitSpawned()) {
			event.cancel()
			ChatUtils.addMessageWithClickAction(
				tr("nobaaddons.chocolateFactory.commandNoMythicRabbit", "Blocked opening Chocolate Factory without a Mythic Rabbit pet!")
					.hoverText(tr("nobaaddons.chocolateFactory.clickPetsMenu", "Click to open the Pets menu").yellow()),
				color = Formatting.RED,
			) { ChatUtils.queueCommand("pets") }
		}
	}

	private fun onSendPacket(event: PacketEvents.Send) {
		if(!config.requireMythicRabbit) return
		val packet = event.packet
		if(packet !is PlayerInteractItemC2SPacket && packet !is PlayerInteractBlockC2SPacket && packet !is PlayerInteractEntityC2SPacket) return

		val player = MCUtils.player ?: return
		val heldItem = player.mainHandStack.getSkyBlockItem() ?: return
		if(heldItem.id != "EGGLOCATOR") return

		if(!hasMythicRabbitSpawned()) {
			RenderUtils.drawTitle(tr("nobaaddons.chocolateFactory.spawnMythicRabbit", "Spawn Mythic Rabbit!"), NobaColor.RED)
		}
	}

	fun hasMythicRabbitSpawned(): Boolean {
		return PetAPI.currentPet?.id == "RABBIT" && PetAPI.currentPet?.rarity == Rarity.MYTHIC
	}
}