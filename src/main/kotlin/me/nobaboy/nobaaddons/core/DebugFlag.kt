package me.nobaboy.nobaaddons.core

import net.minecraft.util.StringIdentifiable
import kotlin.reflect.KProperty

enum class DebugFlag : StringIdentifiable {
	COPY_RAW_CHAT_COMPONENT,
	;

	var enabled = false

	@Suppress("unused")
	operator fun getValue(instance: Any?, property: KProperty<*>) = enabled
	override fun asString() = name
}