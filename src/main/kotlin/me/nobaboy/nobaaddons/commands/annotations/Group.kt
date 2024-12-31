package me.nobaboy.nobaaddons.commands.annotations

/**
 * Annotate an inner `object` class to add it and its containing commands to the parent group;
 * this may also be attached to the root group to change the generated command name
 *
 * @param name The name used for the generated command; by default, this is the lowercased class name
 * @param aliases Optional aliases to also register this group under
 */
@Target(AnnotationTarget.CLASS)
@MustBeDocumented
annotation class Group(val name: String = "", vararg val aliases: String)