package me.nobaboy.nobaaddons.api.skyblock.fishing

import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI
import me.nobaboy.nobaaddons.core.fishing.SeaCreature
import me.nobaboy.nobaaddons.events.impl.chat.ChatMessageEvents
import me.nobaboy.nobaaddons.events.impl.skyblock.FishingEvents
import me.nobaboy.nobaaddons.repo.Repo.fromRepo

object SeaCreatureAPI {
	private val DOUBLE_HOOK_REGEX by Regex("^It's a Double Hook!(?: Woot woot!)?").fromRepo("fishing.double_hook")

	private var doubleHook: Boolean = false

	fun init() {
		ChatMessageEvents.CHAT.register(this::onChatMessage)
	}

	private fun onChatMessage(event: ChatMessageEvents.Chat) {
		if(!SkyBlockAPI.inSkyBlock) return

		val message = event.cleaned

		if(DOUBLE_HOOK_REGEX.matches(message)) {
			doubleHook = true
			return
		}

		val seaCreature = SeaCreature.getBySpawnMessage(message) ?: return
		FishingEvents.SEA_CREATURE_CATCH.invoke(FishingEvents.SeaCreatureCatch(seaCreature, doubleHook))
		doubleHook = false
	}
}