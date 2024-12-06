package me.nobaboy.nobaaddons.utils

import java.util.regex.Matcher
import java.util.regex.Pattern

// TODO switch to using kotlin Regex
object RegexUtils {
	fun Pattern.matches(string: String?): Boolean = string?.let { matcher(it).matches() } == true

	inline fun <T> Pattern.matchMatcher(text: String, consumer: Matcher.() -> T) =
		matcher(text).let { if(it.matches()) consumer(it) else null }

	inline fun <T> Pattern.findMatcher(text: String, consumer: Matcher.() -> T) =
		matcher(text).let { if(it.find()) consumer(it) else null }
	inline fun Pattern.findAllMatcher(text: String, consumer: Matcher.() -> Unit) {
		val matcher = matcher(text)
		while(matcher.find()) {
			consumer(matcher)
		}
	}

	inline fun <T> Pattern.firstMatcher(sequence: Sequence<String>, consumer: Matcher.() -> T): T? {
		for(line in sequence) {
			matcher(line).let { if(it.matches()) return consumer(it) }
		}
		return null
	}
	inline fun <T> Pattern.firstMatcher(list: List<String>, consumer: Matcher.() -> T): T? = firstMatcher(list.asSequence(), consumer)

	inline fun <T> Pattern.matchAll(list: List<String>, consumer: Matcher.() -> T): T? {
		for(line in list) {
			matcher(line).let { if(it.find()) consumer(it) }
		}
		return null
	}

	inline fun List<String>.forEachMatch(pattern: Pattern, consumer: Matcher.() -> Unit) {
		this.forEach { pattern.matchMatcher(it, consumer) }
	}

	inline fun <T> List<Pattern>.matchMatchers(text: String, consumer: Matcher.() -> T): T? {
		for(pattern in iterator()) {
			pattern.matchMatcher<T>(text) {
				return consumer()
			}
		}
		return null
	}

	fun List<Pattern>.allMatches(list: List<String>): List<String> = list.filter { line -> any { it.matches(line) } }
	fun List<Pattern>.anyMatches(list: List<String>?): Boolean = list?.any { line -> any { it.matches(line) } } == true
	fun List<Pattern>.anyMatches(string: String): Boolean = any { it.matches(string) }
}