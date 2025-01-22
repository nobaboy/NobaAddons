package me.nobaboy.nobaaddons.features.chocolatefactory

import me.nobaboy.nobaaddons.api.skyblock.PetAPI
import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI
import me.nobaboy.nobaaddons.api.skyblock.events.hoppity.HoppityAPI
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.core.Rarity
import me.nobaboy.nobaaddons.events.impl.chat.SendMessageEvents
import me.nobaboy.nobaaddons.events.impl.client.InteractEvents
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.TextUtils.hoverText
import me.nobaboy.nobaaddons.utils.TextUtils.yellow
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import me.nobaboy.nobaaddons.utils.items.ItemUtils.skyBlockId
import me.nobaboy.nobaaddons.utils.render.RenderUtils
import me.nobaboy.nobaaddons.utils.tr
import net.minecraft.util.Formatting

object ChocolateFactoryFeatures {
	private val config get() = NobaConfig.INSTANCE.events.hoppity
	private val enabled: Boolean get() = config.requireMythicRabbit && SkyBlockAPI.inSkyBlock

	private val hasMythicRabbitSpawned: Boolean get() =
		PetAPI.currentPet?.id == "RABBIT" && PetAPI.currentPet?.rarity == Rarity.MYTHIC

	fun init() {
		InteractEvents.USE_ITEM.register(this::onInteract)
		SendMessageEvents.SEND_COMMAND.register(this::onSendCommand)
	}

	private fun onInteract(event: InteractEvents.UseItem) {
		if(!HoppityAPI.isSpring || !HoppityAPI.inRelevantIsland) return
		if(!enabled) return

		if(event.itemInHand.skyBlockId != HoppityAPI.LOCATOR) return
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