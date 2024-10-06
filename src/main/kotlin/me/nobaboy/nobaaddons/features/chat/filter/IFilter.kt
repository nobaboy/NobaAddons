package me.nobaboy.nobaaddons.features.chat.filter

import me.nobaboy.nobaaddons.config.impl.ChatFilterOption

interface IFilter {
	fun shouldFilter(option: ChatFilterOption): Boolean {
		return when(option) {
			ChatFilterOption.SHOWN -> false
			ChatFilterOption.COMPACT -> true
			ChatFilterOption.HIDDEN -> true
		}
	}
}