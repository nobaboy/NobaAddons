package me.nobaboy.nobaaddons.utils

object CollectionUtils {
	fun List<String>.anyEquals(other: String, ignoreCase: Boolean = false) = this.any { it.equals(other, ignoreCase = ignoreCase) }
	fun List<String>.anyContains(other: String, ignoreCase: Boolean = false) = this.any { it.contains(other, ignoreCase = ignoreCase) }

	fun <T> List<T>.nextAfter(after: T, skip: Int = 1): T? {
		val index = this.indexOf(after)
		if(index == -1) return null
		return this.getOrNull(index + skip)
	}
}