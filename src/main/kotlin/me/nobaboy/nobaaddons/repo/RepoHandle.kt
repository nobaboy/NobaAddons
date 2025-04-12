package me.nobaboy.nobaaddons.repo

import me.nobaboy.nobaaddons.NobaAddons
import net.fabricmc.loader.api.FabricLoader
import kotlin.reflect.KProperty

/**
 * A handle providing [key] from the provided [source] map
 */
class RepoHandle<T>(val source: RepoSource<Map<String, T>>, val key: String, val fallback: T) {
	private val map by source

	val value: T get() {
		val value = (map ?: return fallback).getOrDefault(key, fallback)
		// be very noisy in a dev env to allow for spotting these kinds of issues
		if(FabricLoader.getInstance().isDevelopmentEnvironment && value != fallback) {
			NobaAddons.LOGGER.warn("{} is being overridden from repo in source {}", key, source)
		}
		return value
	}

	val overridden: Boolean get() = value != fallback

	@Suppress("unused")
	operator fun getValue(instance: Any?, property: KProperty<*>): T = value

	override fun toString(): String = "RepoHandle(source=$source, key=$key, fallback=$fallback, value=$value)"
}