package me.nobaboy.nobaaddons.utils

object CollectionUtils {
	fun <T> List<T>.nextAfter(after: T, skip: Int = 1): T? {
		val index = this.indexOf(after)
		if(index == -1) return null
		return this.getOrNull(index + skip)
	}
}