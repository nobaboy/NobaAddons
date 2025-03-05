package me.nobaboy.nobaaddons.core.events

import me.nobaboy.nobaaddons.utils.TextUtils.toText
import net.minecraft.text.Text
import net.minecraft.util.Formatting

enum class MythologicalMobs(val displayName: String) {
	MINOS_HUNTER("Minos Hunter"),
	SIAMESE_LYNX("Siamese Lynxes"),
	MINOTAUR("Minotaur"),
	GAIA_CONSTRUCT("Gaia Construct"),
	MINOS_CHAMPION("Minos Champion"),
	MINOS_INQUISITOR("Minos Inquisitor");

	fun toText(): Text = displayName.toText().formatted(Formatting.GREEN)

	companion object {
		fun getByName(name: String): MythologicalMobs? = entries.firstOrNull { it.displayName == name }
	}
}