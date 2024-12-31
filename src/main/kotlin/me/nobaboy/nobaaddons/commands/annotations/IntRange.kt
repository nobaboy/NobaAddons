package me.nobaboy.nobaaddons.commands.annotations

/**
 * Used on [Int] arguments to control the allowed range
 *
 * ## Example
 *
 * ```kt
 * fun command(ctx: Context, number: @IntRange(1, 100) Int) {
 *     // ...
 * }
 * ```
 */
@Target(AnnotationTarget.TYPE)
annotation class IntRange(val min: Int = Int.MIN_VALUE, val max: Int = Int.MAX_VALUE)