package me.nobaboy.nobaaddons.commands.impl

import kotlin.reflect.KFunction

class NobaShortClientCommand(
	override val name: String,
	function: KFunction<*>,
	instance: Any?,
	override val aliases: List<String> = emptyList(),
) : NobaClientCommand(function, instance)