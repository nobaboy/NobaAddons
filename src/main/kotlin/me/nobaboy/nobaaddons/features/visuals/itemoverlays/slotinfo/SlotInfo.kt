package me.nobaboy.nobaaddons.features.visuals.itemoverlays.slotinfo

import net.minecraft.text.Text

data class SlotInfo(
	val text: Text,
	val position: Position = Position.TOP_LEFT
)