package me.nobaboy.nobaaddons.utils

object RegexUtils {
	/**
	 * Executes [consumer] with the resulting [MatchResult] if the provided [text] fully matches the current pattern
	 */
	inline fun Regex.onFullMatch(text: String, consumer: MatchResult.() -> Unit) = matchEntire(text)?.let(consumer)

	/**
	 * Executes [consumer] with the resulting [MatchResult] if the provided [text] partially matches the current pattern
	 *
	 * Note that this only returns the first partial match; if you need *all* matches, you should use [forEachMatch] instead.
	 */
	inline fun Regex.onPartialMatch(text: String, consumer: MatchResult.() -> Unit) = find(text)?.let(consumer)

	/**
	 * Same as [onFullMatch], but returns the return value of [consumer]
	 */
	inline fun <T> Regex.mapFullMatch(text: String, consumer: MatchResult.() -> T): T? = matchEntire(text)?.let(consumer)

	/**
	 * Executes [consumer] for each partial match of the current pattern on [text]
	 */
	inline fun Regex.forEachMatch(text: String, consumer: MatchResult.() -> Unit) = findAll(text).forEach(consumer)

	/**
	 * Executes [consumer] for the first line from [lines] matching the current pattern
	 */
	inline fun Regex.firstFullMatch(lines: Collection<String>, consumer: MatchResult.() -> Unit) =
		lines.firstNotNullOfOrNull { matchEntire(it) }?.let(consumer)

	/**
	 * Executes [consumer] for each line in [lines] that fully matches the current pattern
	 */
	inline fun Regex.forEachFullMatch(lines: Collection<String>, crossinline consumer: MatchResult.() -> Unit) =
		lines.forEach { onFullMatch(it, consumer) }

	/**
	 * Executes [consumer] with the first full match from any of the patterns in the current iterable
	 */
	inline fun Iterable<Regex>.firstFullMatch(text: String, consumer: MatchResult.() -> Unit) =
		this.firstNotNullOfOrNull { it.matchEntire(text) }?.let(consumer)

	/**
	 * Executes [consumer] with the first partial match from any of the patterns in the current iterable
	 */
	inline fun Iterable<Regex>.firstPartialMatch(text: String, consumer: MatchResult.() -> Unit) =
		this.firstNotNullOfOrNull { it.find(text) }?.let(consumer)

	/**
	 * Returns `true` if any pattern in the current iterable fully matches the provided string
	 */
	fun Iterable<Regex>.anyFullMatch(text: String): Boolean =
		this.firstNotNullOfOrNull { it.matchEntire(text) } != null

	/**
	 * Returns the value of the requested group from the given [text] if it fully matches the current pattern
	 */
	fun Regex.getGroupFromFullMatch(text: String, group: String): String? = mapFullMatch(text) { groups[group]?.value }
}