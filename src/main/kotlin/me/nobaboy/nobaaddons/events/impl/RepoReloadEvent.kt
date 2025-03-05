package me.nobaboy.nobaaddons.events.impl

import me.nobaboy.nobaaddons.events.AbstractEvent
import me.nobaboy.nobaaddons.events.EventDispatcher

class RepoReloadEvent : AbstractEvent() {
	companion object : EventDispatcher<RepoReloadEvent>()
}