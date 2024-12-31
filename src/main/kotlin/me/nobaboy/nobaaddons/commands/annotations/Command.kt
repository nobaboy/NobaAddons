package me.nobaboy.nobaaddons.commands.annotations

/**
 * Annotate a function in a class with this method to make it a command usable in-game
 *
 * Using [RootCommand] alongside this annotation will result in an error at runtime
 *
 * The annotated function may be a `suspend fun`, and will automatically be wrapped in an async context
 * as necessary
 *
 * @param name The name used for the generated command; by default, this is the lowercased function name
 * @param aliases Optional aliases to also register this command under
 */
@Target(AnnotationTarget.FUNCTION)
@MustBeDocumented
annotation class Command(val name: String = "", vararg val aliases: String)