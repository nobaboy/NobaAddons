package me.nobaboy.nobaaddons.features.mining.glacitemineshaft

import dev.isxander.yacl3.api.NameableEnum
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.tr
import net.minecraft.text.Text

enum class CorpseType(
	val color: NobaColor,
	private val helmetId: String,
	private val keyId: String? = null,
) : NameableEnum {
	LAPIS(NobaColor.BLUE, "LAPIS_ARMOR_HELMET"),
	UMBER(NobaColor.GOLD, "ARMOR_OF_YOG_HELMET", "UMBER_KEY"),
	TUNGSTEN(NobaColor.GRAY, "MINERAL_HELMET", "UMBER_KEY"),
	VANGUARD(NobaColor.AQUA, "VANGUARD_HELMET", "SKELETON_KEY"),
	;

	val formattedName: Text by lazy { displayName.copy().formatted(color.formatting) }

	override fun getDisplayName(): Text = when(this) {
		LAPIS -> tr("nobaaddons.label.glaciteMineshaft.corpseType.lapis", "Lapis Corpse")
		UMBER -> tr("nobaaddons.label.glaciteMineshaft.corpseType.lapis", "Umber Corpse")
		TUNGSTEN -> tr("nobaaddons.label.glaciteMineshaft.corpseType.lapis", "Tungsten Corpse")
		VANGUARD -> tr("nobaaddons.label.glaciteMineshaft.corpseType.lapis", "Vanguard Corpse")
	}

	companion object {
		fun getByHelmetOrNull(helmetId: String): CorpseType? = entries.firstOrNull { it.helmetId == helmetId }
		fun getByKeyOrNull(keyId: String): CorpseType? = entries.firstOrNull { it.keyId == keyId }
	}
}