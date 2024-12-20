package me.nobaboy.nobaaddons.repo

import com.google.gson.JsonObject
import me.nobaboy.nobaaddons.repo.Repo.readJson
import kotlin.reflect.KProperty

object RepoRegex : IRepoObject {
	private const val FILE = "regexes.json"
	var regexes: Map<String, Regex> = emptyMap()
		private set

	override fun load() {
		regexes = Repo.REPO_DIRECTORY.resolve(FILE)
			.readJson(JsonObject::class.java)
			.asMap()
			.mapValues { Regex(it.value.asString) }
	}

	class RegexEntry(private val key: String, private val fallback: Regex) {
		fun get(): Regex = regexes.getOrDefault(key, fallback)
		operator fun getValue(instance: Any, property: KProperty<*>): Regex = get()
	}

	class RegexEntries(private val entries: Collection<RegexEntry>) {
		fun get(): List<Regex> = entries.map { it.get() }
		operator fun getValue(instance: Any, property: KProperty<*>): List<Regex> = get()
	}
}