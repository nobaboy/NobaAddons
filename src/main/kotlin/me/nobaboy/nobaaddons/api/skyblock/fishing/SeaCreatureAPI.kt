package me.nobaboy.nobaaddons.api.skyblock.fishing

import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.core.fishing.SeaCreature
import me.nobaboy.nobaaddons.events.impl.chat.ChatMessageEvents
import me.nobaboy.nobaaddons.events.impl.skyblock.FishingEvents
import me.nobaboy.nobaaddons.repo.Repo.fromRepo
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import me.nobaboy.nobaaddons.utils.TextUtils.bold
import me.nobaboy.nobaaddons.utils.TextUtils.buildText
import me.nobaboy.nobaaddons.utils.TextUtils.green
import me.nobaboy.nobaaddons.utils.TextUtils.toText
import me.nobaboy.nobaaddons.utils.TextUtils.yellow
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import me.nobaboy.nobaaddons.utils.chat.Message
import me.nobaboy.nobaaddons.utils.tr

object SeaCreatureAPI {
	private val config get() = NobaConfig.INSTANCE.fishing.catchMessages

	private val DOUBLE_HOOK_REGEX by Regex("^It's a Double Hook!(?: Woot woot!)?").fromRepo("fishing.double_hook")

	private var lastChatMessage: Message? = null
	private var doubleHook: Boolean = false

	fun init() {
		ChatMessageEvents.ALLOW.register(this::onChatMessage)
	}

	private fun onChatMessage(event: ChatMessageEvents.Allow) {
		if(!SkyBlockAPI.inSkyBlock) return

		val message = event.message.string.cleanFormatting()

		if(DOUBLE_HOOK_REGEX.matches(message)) {
			if(config.compactSeaCreatureMessages || config.hideCatchMessage) event.cancel()
			doubleHook = true
			return
		}

		val seaCreature = SeaCreature.getBySpawnMessage(message) ?: return

		if(config.compactSeaCreatureMessages) {
			event.cancel()

			if(!config.hideCatchMessage || seaCreature.rarity > config.hideMaxRarity) {
				if(config.removeLastCatchMessage) lastChatMessage?.remove()
				lastChatMessage = ChatUtils.addAndCaptureMessage(compileCatchMessage(seaCreature.spawnMessage), prefix = false, color = null)
			}
		}

		FishingEvents.SEA_CREATURE_CATCH.invoke(FishingEvents.SeaCreatureCatch(seaCreature, doubleHook))
		doubleHook = false
	}

	private fun compileCatchMessage(spawnMessage: String) = buildText {
		if(doubleHook) {
			append(tr("nobaaddons.fishing.doubleHook", "DOUBLE HOOK!").yellow().bold())
			append(" ")
		}

		append(spawnMessage.toText().green())
	}
}