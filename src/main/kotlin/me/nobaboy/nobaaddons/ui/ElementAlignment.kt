package me.nobaboy.nobaaddons.ui

import net.minecraft.util.StringIdentifiable

enum class ElementAlignment : StringIdentifiable {
	LEFT,
	CENTER,
	RIGHT,
	;

	override fun asString(): String = name
}