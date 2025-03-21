package me.nobaboy.nobaaddons.api.skyblock.fishing

import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI
import me.nobaboy.nobaaddons.core.fishing.SeaCreature
import me.nobaboy.nobaaddons.events.impl.chat.ChatMessageEvents
import me.nobaboy.nobaaddons.events.impl.skyblock.FishingEvents
import me.nobaboy.nobaaddons.repo.Repo.fromRepo
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting

object FishingAPI {
	private val DOUBLE_HOOK_REGEX by Regex("^It's a Double Hook!(?: Woot woot!)?").fromRepo("fishing.double_hook")

	private var doubleHookEvent: ChatMessageEvents.Allow? = null

	fun init() {
		ChatMessageEvents.ALLOW.register(this::onChatMessage)
	}

	private fun onChatMessage(event: ChatMessageEvents.Allow) {
		if(!SkyBlockAPI.inSkyBlock) return

		val message = event.message.string.cleanFormatting()

		if(DOUBLE_HOOK_REGEX.matches(message)) doubleHookEvent = event

		val seaCreature = SeaCreature.getBySpawnMessage(message) ?: return
		FishingEvents.SEA_CREATURE_CATCH.invoke(FishingEvents.SeaCreatureCatch(seaCreature, doubleHookEvent != null))
		doubleHookEvent = null
	}
}