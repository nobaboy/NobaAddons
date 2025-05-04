package me.nobaboy.nobaaddons.utils;

import net.minecraft.command.argument.EnumArgumentType;
import net.minecraft.util.StringIdentifiable;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Various utilities that can't be implemented in Kotlin for various reasons, like intersection type definitions.
 */
public final class JavaUtils {
	private JavaUtils() {
		throw new UnsupportedOperationException();
	}

	@NotNull
	public static <T extends Enum<T> & StringIdentifiable> EnumArgumentType<T> enumArgument(@NotNull Class<T> cls) {
		return new EnumArgumentType<>(StringIdentifiable.createBasicCodec(cls::getEnumConstants), cls::getEnumConstants) {};
	}

	@NotNull
	public static <T extends Enum<T> & StringIdentifiable> EnumArgumentType<T> enumArgument(@NotNull Supplier<@NotNull T[]> only) {
		return new EnumArgumentType<>(StringIdentifiable.createBasicCodec(only), only) {};
	}
}
