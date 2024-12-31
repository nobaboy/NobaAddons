package me.nobaboy.nobaaddons.commands.annotations

import java.util.function.Supplier
import kotlin.reflect.KClass

/**
 * Only register the current [Command] or [Group] if the provided [predicate] succeeds;
 * the provided class **must** be an `object` class
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@MustBeDocumented
annotation class EnabledIf(val predicate: KClass<out Supplier<Boolean>>)