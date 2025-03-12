package me.nobaboy.nobaaddons.utils

import net.minecraft.util.function.ValueLists
import java.util.function.IntFunction
import java.util.function.ToIntFunction

object EnumUtils {
	inline fun <reified T : Enum<T>> ordinalMapper(outOfBounds: ValueLists.OutOfBoundsHandling = ValueLists.OutOfBoundsHandling.WRAP): IntFunction<T> {
		return ValueLists./*? if >=1.21.5-pre2 {*//*createIndexToValueFunction*//*?} else {*/createIdToValueFunction/*?}*/(
			ToIntFunction<T> { it.ordinal }, enumValues<T>(), outOfBounds
		)
	}
}