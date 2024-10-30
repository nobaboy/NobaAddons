package me.nobaboy.nobaaddons.core

import dev.isxander.yacl3.api.NameableEnum
import me.nobaboy.nobaaddons.utils.StringUtils.title
import me.nobaboy.nobaaddons.utils.TextUtils.toText
import net.minecraft.text.Text

enum class MobRarity : NameableEnum {
	COMMON,
	UNCOMMON,
	RARE,
	EPIC,
	LEGENDARY,
	MYTHIC;

	override fun getDisplayName(): Text = name.title().toText()

	fun isAtLeast(rarity: MobRarity): Boolean = this.ordinal >= rarity.ordinal
}