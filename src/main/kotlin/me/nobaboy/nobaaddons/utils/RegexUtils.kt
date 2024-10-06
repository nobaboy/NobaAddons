package me.nobaboy.nobaaddons.utils

import java.util.regex.Matcher
import java.util.regex.Pattern

object RegexUtils {
	fun Pattern.matches(string: String?) = string?.let { matcher(it).matches() } == true

	inline fun <T> Pattern.matchMatcher(text: String, consumer: Matcher.() -> T) =
		matcher(text).let { if(it.matches()) consumer(it) else null }
	inline fun <T> Pattern.findMatcher(text: String, consumer: Matcher.() -> T) =
		matcher(text).let { if(it.find()) consumer(it) else null }
	inline fun <T> Pattern.findAllMatcher(text: String, consumer: Matcher.() -> T) {
		val matcher = matcher(text)
		while (matcher.find()) {
			consumer(matcher)
		}
	}

	inline fun <T> List<String>.matchFirst(pattern: Pattern, consumer: Matcher.() -> T): T? {
		this.forEach { line ->
			pattern.matcher(line).let { if(it.matches()) return consumer(it) }
		}
		return null
	}
}