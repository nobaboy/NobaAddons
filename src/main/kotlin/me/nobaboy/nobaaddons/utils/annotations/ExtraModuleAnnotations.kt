package me.nobaboy.nobaaddons.utils.annotations

import me.owdding.ktmodules.AutoCollect

@AutoCollect("Configs")
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class ConfigModule

@AutoCollect("CoreModules")
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class CoreModule

@AutoCollect("Apis")
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class ApiModule