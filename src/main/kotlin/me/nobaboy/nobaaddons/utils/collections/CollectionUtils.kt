package me.nobaboy.nobaaddons.utils.collections

object CollectionUtils {
	fun List<String>.anyEquals(other: String, ignoreCase: Boolean = false): Boolean =
		any { it.equals(other, ignoreCase = ignoreCase) }

	fun List<String>.anyContains(other: String, ignoreCase: Boolean = false): Boolean =
		any { it.contains(other, ignoreCase = ignoreCase) }

	fun <T> List<T>.nextAfter(after: T, skip: Int = 1): T? {
		val index = indexOf(after)
		if(index == -1) return null
		return getOrNull(index + skip)
	}
}