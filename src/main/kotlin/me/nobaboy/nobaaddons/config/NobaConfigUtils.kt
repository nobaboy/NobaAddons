package me.nobaboy.nobaaddons.config

import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder

object NobaConfigUtils {
    fun createBooleanController(option: Option<Boolean>): BooleanControllerBuilder {
        return BooleanControllerBuilder.create(option).yesNoFormatter().coloured(true)
    }
}