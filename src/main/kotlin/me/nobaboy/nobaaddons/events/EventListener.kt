package me.nobaboy.nobaaddons.events

/**
 * Annotate on a function to mark it as a listener; this is primarily only used by features.
 *
 * ## Example
 *
 * ```kt
 * @EventListener
 * fun eventListener(event: EventClass) {
 *     // ...
 * }
 * ```
 */
@MustBeDocumented
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class EventListener