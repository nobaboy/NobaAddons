package me.nobaboy.nobaaddons.features

import me.nobaboy.nobaaddons.utils.tr
import net.minecraft.text.Text

enum class FeatureCategory(val displayName: Text) {
	GENERAL(tr("nobaaddons.category.general", "General")),

	//

	DEV(tr("nobaaddons.category.dev", "Dev")),
	API(tr("nobaaddons.category.api", "APIs")),
	;
}