package me.nobaboy.nobaaddons.commands.annotations

/**
 * Attach to **at most one** command in a [Group] to make it process commands when no subcommand is used.
 *
 * Using [Command] alongside this annotation will result in an error at runtime
 */
@Target(AnnotationTarget.FUNCTION)
@MustBeDocumented
annotation class RootCommand