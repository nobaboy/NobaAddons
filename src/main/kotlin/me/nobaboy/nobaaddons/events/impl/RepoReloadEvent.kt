package me.nobaboy.nobaaddons.events.impl

import me.nobaboy.nobaaddons.events.Event
import me.nobaboy.nobaaddons.events.EventDispatcher

class RepoReloadEvent : Event() {
	companion object {
		val EVENT = EventDispatcher<RepoReloadEvent>()
	}
}