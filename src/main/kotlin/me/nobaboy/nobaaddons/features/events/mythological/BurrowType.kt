package me.nobaboy.nobaaddons.features.events.mythological

import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.tr
import net.minecraft.text.Text

enum class BurrowType(val displayName: Text, val color: NobaColor) {
	START(tr("nobaaddons.label.burrowType.start", "Start"), NobaColor.GREEN),
	MOB(tr("nobaaddons.label.burrowType.mob", "Mob"), NobaColor.RED),
	TREASURE(tr("nobaaddons.label.burrowType.treasure", "Treasure"), NobaColor.GOLD),
	UNKNOWN(tr("nobaaddons.label.burrowType.unknown", "Unknown"), NobaColor.GRAY)
}