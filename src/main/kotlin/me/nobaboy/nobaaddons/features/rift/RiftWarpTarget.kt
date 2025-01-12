package me.nobaboy.nobaaddons.features.rift

import dev.isxander.yacl3.api.NameableEnum
import me.nobaboy.nobaaddons.utils.tr

enum class RiftWarpTarget(val warpName: String) : NameableEnum {
	WIZARD_TOWER("wiz"),
	RIFT("rift");

	override fun getDisplayName() = when(this) {
		WIZARD_TOWER -> tr("nobaaddons.label.riftWarpTarget.wizardTower", "Wizard Tower")
		RIFT -> tr("nobaaddons.label.riftWarpTarget.rift", "The Rift")
	}
}
