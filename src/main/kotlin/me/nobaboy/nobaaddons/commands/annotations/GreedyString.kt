package me.nobaboy.nobaaddons.commands.annotations

/**
 * Used on [String] arguments to make them use [com.mojang.brigadier.arguments.StringArgumentType.greedyString] instead
 *
 * No other arguments may follow a greedy string
 *
 * ## Example
 *
 * ```kt
 * fun command(ctx: Context, string: @GreedyString String) {
 *     // ...
 * }
 * ```
 */
@Target(AnnotationTarget.TYPE)
annotation class GreedyString