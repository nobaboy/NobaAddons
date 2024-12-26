package me.nobaboy.nobaaddons.utils.annotations

/**
 * Methods annotated with this allow for sending untranslated messages; you should avoid using these outside of
 * messages that are only expected to be seen in a development environment, or otherwise are largely untranslatable,
 * and instead use `tr`.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
@RequiresOptIn
annotation class UntranslatedMessage