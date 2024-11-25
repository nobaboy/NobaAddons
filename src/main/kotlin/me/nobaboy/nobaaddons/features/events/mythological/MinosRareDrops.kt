package me.nobaboy.nobaaddons.features.events.mythological

import me.nobaboy.nobaaddons.events.InventoryEvents
import me.nobaboy.nobaaddons.utils.SoundUtils
import me.nobaboy.nobaaddons.utils.TextUtils.buildText
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import me.nobaboy.nobaaddons.utils.items.ItemUtils.getSkyBlockItem
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import kotlin.time.Duration.Companion.seconds

object MinosRareDrops {
	private val droppedItems = mutableListOf<String>()

	private val rareDrops = listOf(
		"ANTIQUE_REMEDIES",
		"CROCHET_TIGER_PLUSHIE",
		"DWARF_TURTLE_SHELMET",
		"MINOS_RELIC"
	)

	fun init() {
		InventoryEvents.SLOT_UPDATE.register(this::onSlotUpdate)
	}

	private fun onSlotUpdate(event: InventoryEvents.SlotUpdate) {
		val itemStack = event.itemStack
		val item = itemStack.getSkyBlockItem() ?: return
		if(item.id !in rareDrops) return

		item.timestamp?.let { if(it.elapsedSince() > 3.seconds) return } ?: return
		item.uuid?.let {
			if(it in droppedItems) return
			droppedItems.add(it)
			if(droppedItems.size > 8) droppedItems.removeFirst()
		} ?: return

		val text = buildText {
			append(Text.literal("RARE DROP! ").formatted(Formatting.GOLD, Formatting.BOLD))
			append(itemStack.name)
		}

		ChatUtils.addMessage(text, prefix = false)
		SoundUtils.rareDropSound.play()
	}
}