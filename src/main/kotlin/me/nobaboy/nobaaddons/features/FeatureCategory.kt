package me.nobaboy.nobaaddons.features

import me.nobaboy.nobaaddons.config.core.CoreAPIConfig
import me.nobaboy.nobaaddons.config.option.AbstractConfigOptionHolder
import me.nobaboy.nobaaddons.utils.tr
import net.minecraft.text.Text

enum class FeatureCategory(val displayName: Text, val global: AbstractConfigOptionHolder? = null) {
	GENERAL(tr("nobaaddons.category.general", "General")),

	UI_AND_VISUAL(tr("nobaaddons.category.uiAndVisual", "UI & Visual")),
	INVENTORY(tr("nobaaddons.category.inventory", "Inventory")),
	EVENTS(tr("nobaaddons.category.events", "Events")),
	SLAYER(tr("nobaaddons.category.slayers", "Slayers")),
	FISHING(tr("nobaaddons.category.fishing", "Fishing")),
	MINING(tr("nobaaddons.category.mining", "Mining")),
	DUNGEONS(tr("nobaaddons.category.dungeons", "Dungeons")),
	RIFT(tr("nobaaddons.category.rift", "Rift")),
	CHAT(tr("nobaaddons.category.chat", "Chat")),
	QOL(tr("nobaaddons.category.qol", "QOL")),

	DEV(tr("nobaaddons.category.dev", "Dev")),
	API(tr("nobaaddons.category.api", "APIs"), CoreAPIConfig),
	;
}