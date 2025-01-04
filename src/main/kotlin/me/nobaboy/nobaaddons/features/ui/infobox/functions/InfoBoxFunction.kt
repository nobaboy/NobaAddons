package me.nobaboy.nobaaddons.features.ui.infobox.functions

interface InfoBoxFunction<T> {
	val name: String
	val aliases: List<String> get() = emptyList()
	fun execute(): T
}