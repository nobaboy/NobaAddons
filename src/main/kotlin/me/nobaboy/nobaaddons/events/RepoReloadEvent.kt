package me.nobaboy.nobaaddons.events

import me.nobaboy.nobaaddons.events.internal.Event
import me.nobaboy.nobaaddons.events.internal.EventDispatcher

class RepoReloadEvent : Event() {
	companion object {
		val EVENT = EventDispatcher<RepoReloadEvent>()

		fun invoke() = EVENT.invoke(RepoReloadEvent())
	}
}