package me.nobaboy.nobaaddons.features.events.mythological

import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.tr
import net.minecraft.text.Text

enum class BurrowType(val displayName: Text, val color: NobaColor) {
	START(tr("nobaaddons.label.mythological.burrowType.start", "Start"), NobaColor.GREEN),
	MOB(tr("nobaaddons.label.mythological.burrowType.mob", "Mob"), NobaColor.RED),
	TREASURE(tr("nobaaddons.label.mythological.burrowType.treasure", "Treasure"), NobaColor.GOLD),
	UNKNOWN(tr("nobaaddons.label.mythological.burrowType.unknown", "Unknown"), NobaColor.GRAY)
}