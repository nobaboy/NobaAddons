package me.nobaboy.nobaaddons.repo

class RepoSourceHandleProvider<T>(val source: RepoSource<Map<String, T>>) {
	private val lock = Any()
	internal val handles: MutableMap<String, RepoHandle<T>> = mutableMapOf()

	fun get(key: String, fallback: T): RepoHandle<T> = synchronized(lock) {
		check(key !in handles) { "$key has already been used" }
		val handle = RepoHandle(source, key, fallback)
		handles[key] = handle
		handle
	}
}