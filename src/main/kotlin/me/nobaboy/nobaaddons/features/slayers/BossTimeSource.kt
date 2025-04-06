package me.nobaboy.nobaaddons.features.slayers

import dev.isxander.yacl3.api.NameableEnum
import me.nobaboy.nobaaddons.utils.tr
import net.minecraft.text.Text

enum class BossTimeSource : NameableEnum {
	REAL_TIME,
	BOSS_TIME_REMAINING,
	;

	override fun getDisplayName(): Text = when(this) {
		REAL_TIME -> tr("nobaaddons.label.bossTimeSource.realTime", "Real Time")
		BOSS_TIME_REMAINING -> tr("nobaaddons.label.bossTimeSource.bossTimeRemaining", "Boss Time Remaining")
	}
}