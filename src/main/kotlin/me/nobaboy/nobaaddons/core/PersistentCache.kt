package me.nobaboy.nobaaddons.core

import dev.celestialfault.histoire.Histoire
import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.config.util.saveOnExit
import me.nobaboy.nobaaddons.features.chat.channeldisplay.ActiveChatChannel
import me.nobaboy.nobaaddons.features.chat.channeldisplay.ChatChannel
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
object PersistentCache : Histoire(NobaAddons.CONFIG_DIR.resolve("cache.json").toFile()) {
	init {
		saveOnExit()
	}

	var lastProfile: Uuid? = null
	var repoCommit: String? = null
	var devMode: Boolean = false
	var channel: ActiveChatChannel = ActiveChatChannel(ChatChannel.UNKNOWN)
}