package me.nobaboy.nobaaddons.features.events.mythological

import dev.isxander.yacl3.api.NameableEnum
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.tr
import net.minecraft.text.Text

enum class BurrowType(val color: NobaColor) : NameableEnum {
	START(NobaColor.GREEN),
	MOB(NobaColor.RED),
	TREASURE(NobaColor.GOLD),
	UNKNOWN(NobaColor.GRAY),
	;

	override fun getDisplayName(): Text = when(this) {
		START -> tr("nobaaddons.label.mythological.burrowType.start", "Start")
		MOB -> tr("nobaaddons.label.mythological.burrowType.mob", "Mob")
		TREASURE -> tr("nobaaddons.label.mythological.burrowType.treasure", "Treasure")
		UNKNOWN -> tr("nobaaddons.label.mythological.burrowType.unknown", "Unknown")
	}
}