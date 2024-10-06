package me.nobaboy.nobaaddons.config

import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder
import dev.isxander.yacl3.api.controller.EnumControllerBuilder

object NobaConfigUtils {
	fun createBooleanController(option: Option<Boolean>): BooleanControllerBuilder {
		return BooleanControllerBuilder.create(option).yesNoFormatter().coloured(true)
	}

	inline fun <reified E : Enum<E>> createCyclingController(option: Option<E>): EnumControllerBuilder<E> {
		return EnumControllerBuilder.create(option).enumClass(E::class.java)
	}
}