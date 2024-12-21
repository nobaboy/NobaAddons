package me.nobaboy.nobaaddons.utils

import java.util.regex.Matcher
import java.util.regex.Pattern

object RegexUtils {
	/**
	 * Executes [consumer] with the resulting [MatchResult] if the provided [text] fully matches the current pattern
	 */
	inline fun Regex.onFullMatch(text: String, consumer: MatchResult.() -> Unit) = matchEntire(text)?.let(consumer)

	/**
	 * Same as [onFullMatch], but returns the return value of [consumer]
	 */
	inline fun <T> Regex.mapFullMatch(text: String, consumer: MatchResult.() -> T): T? = matchEntire(text)?.let(consumer)

	/**
	 * Executes [consumer] for each partial match of the current regex on [text]
	 */
	inline fun Regex.forEachMatch(text: String, consumer: MatchResult.() -> Unit) = findAll(text).forEach(consumer)

	/**
	 * Executes [consumer] for each line in [lines] that fully matches the current pattern
	 */
	inline fun Regex.forEachFullMatch(lines: Collection<String>, crossinline consumer: MatchResult.() -> Unit) =
		lines.forEach { onFullMatch(it, consumer) }

	/**
	 * Executes [consumer] for the first line from [lines] matching the current pattern
	 */
	inline fun Regex.firstFullMatch(lines: Collection<String>, consumer: MatchResult.() -> Unit) =
		lines.firstNotNullOfOrNull { matchEntire(it) }?.let(consumer)

	/**
	 * Returns the value of the requested group from the given [text] if it fully matches the current pattern
	 */
	fun Regex.getGroupFromFullMatch(text: String, group: String): String? = mapFullMatch(text) { groups[group]?.value }

	@Deprecated("Use Regex instead")
	fun Pattern.matches(string: String?): Boolean = string?.let { matcher(it).matches() } == true

	@Deprecated("Use Regex instead")
	inline fun <T> Pattern.matchMatcher(text: String, consumer: Matcher.() -> T) =
		matcher(text).let { if(it.matches()) consumer(it) else null }

	@Deprecated("Use Regex instead")
	inline fun <T> Pattern.findMatcher(text: String, consumer: Matcher.() -> T) =
		matcher(text).let { if(it.find()) consumer(it) else null }

	@Deprecated("Use Regex instead")
	inline fun Pattern.findAllMatcher(text: String, consumer: Matcher.() -> Unit) {
		val matcher = matcher(text)
		while(matcher.find()) {
			consumer(matcher)
		}
	}

	@Deprecated("Use Regex instead")
	inline fun <T> Pattern.firstMatcher(sequence: Sequence<String>, consumer: Matcher.() -> T): T? {
		for(line in sequence) {
			matcher(line).let { if(it.matches()) return consumer(it) }
		}
		return null
	}

	@Deprecated("Use Regex instead")
	inline fun <T> Pattern.firstMatcher(list: List<String>, consumer: Matcher.() -> T): T? = firstMatcher(list.asSequence(), consumer)

	@Deprecated("Use Regex instead")
	inline fun List<String>.forEachMatch(pattern: Pattern, consumer: Matcher.() -> Unit) {
		this.forEach { pattern.matchMatcher(it, consumer) }
	}

	@Deprecated("Use Regex instead")
	inline fun <T> List<Pattern>.matchMatchers(text: String, consumer: Matcher.() -> T): T? {
		for(pattern in iterator()) {
			pattern.matchMatcher<T>(text) {
				return consumer()
			}
		}
		return null
	}

	@Deprecated("Use Regex instead")
	fun List<Pattern>.anyMatches(string: String): Boolean = any { it.matches(string) }
}