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
import me.nobaboy.nobaaddons.utils.TextUtils.yellow
import me.nobaboy.nobaaddons.utils.tr

object SeaCreatureAPI {
	private val config get() = NobaConfig.fishing.catchMessages

	private val DOUBLE_HOOK_REGEX by Regex("^It's a Double Hook!(?: Woot woot!)?").fromRepo("fishing.double_hook")

	private var doubleHook: Boolean = false

	fun init() {
		ChatMessageEvents.ALLOW.register(this::onChatMessage)
		ChatMessageEvents.MODIFY.register(this::modifyMessage)
	}

	private fun onChatMessage(event: ChatMessageEvents.Allow) {
		if(!SkyBlockAPI.inSkyBlock) return

		val message = event.message.string.cleanFormatting()

		if(DOUBLE_HOOK_REGEX.matches(message)) {
			if(config.compactSeaCreatureMessages) event.cancel()
			doubleHook = true
		}
	}

	private fun modifyMessage(event: ChatMessageEvents.Modify) {
		if(!SkyBlockAPI.inSkyBlock) return

		val message = event.message

		val seaCreature = SeaCreature.getBySpawnMessage(message.string.cleanFormatting()) ?: return
		FishingEvents.SEA_CREATURE_CATCH.invoke(FishingEvents.SeaCreatureCatch(seaCreature, doubleHook))

		if(config.compactSeaCreatureMessages) {
			event.message = buildText {
				if(doubleHook) {
					append(tr("nobaaddons.fishing.doubleHook.prefix", "DOUBLE HOOK!").yellow().bold())
					append(" ")
				}

				message.siblings.forEach(::append)
				style = message.style
			}
		}

		doubleHook = false
	}
}