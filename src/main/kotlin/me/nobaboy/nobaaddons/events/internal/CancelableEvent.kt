package me.nobaboy.nobaaddons.events.internal

import org.jetbrains.annotations.ApiStatus

// TODO merge this into Event
@ApiStatus.Obsolete
abstract class CancelableEvent : Event(isCancelable = true)