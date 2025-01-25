package me.nobaboy.nobaaddons.repo.objects

import kotlinx.serialization.KSerializer
import me.nobaboy.nobaaddons.events.impl.RepoReloadEvent
import me.nobaboy.nobaaddons.repo.Repo
import kotlin.reflect.KProperty

class RepoObject<T : Any>(private val path: String, private val serializer: KSerializer<T>) : IRepoObject {
	private var onReload: ((T?) -> Unit)? = null

	init {
		RepoReloadEvent.EVENT.register { this.load() }
	}

	@Volatile private var instance: T? = null

	@Suppress("unused")
	operator fun getValue(instance: Any?, property: KProperty<*>): T? = this.instance

	override fun load() {
		instance = Repo.JSON.decodeFromString(serializer, Repo.readAsString(path))
		onReload?.invoke(instance)
	}

	fun onReload(listener: (T?) -> Unit): RepoObject<T> {
		this.onReload = listener
		return this
	}

	override fun toString(): String = "RepoObject(value=$instance, repoPath=$path)"
}