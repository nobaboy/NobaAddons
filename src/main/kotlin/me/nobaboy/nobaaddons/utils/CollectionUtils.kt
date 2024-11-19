package me.nobaboy.nobaaddons.utils

object CollectionUtils {
	fun List<String>.nextAfter(after: String, skip: Int = 1): String? {
		val index = this.indexOf(after)
		if(index == -1) return null
		return this.getOrNull(index + skip)
	}

	fun <K, V> Map<K, V>.editCopy(function: MutableMap<K, V>.() -> Unit) = toMutableMap().also { function(it) }.toMap()
}