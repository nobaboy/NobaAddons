package me.nobaboy.nobaaddons.features.events.mythological

import me.nobaboy.nobaaddons.events.impl.client.InventoryEvents
import me.nobaboy.nobaaddons.utils.TextUtils.buildText
import me.nobaboy.nobaaddons.utils.collections.TimedSet
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import me.nobaboy.nobaaddons.utils.items.ItemUtils.asSkyBlockItem
import me.nobaboy.nobaaddons.utils.sound.SoundUtils
import me.nobaboy.nobaaddons.utils.tr
import net.minecraft.util.Formatting
import kotlin.time.Duration.Companion.seconds

object AnnounceRareDrops {
	private val uuidCache = TimedSet<String>(10.seconds)

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
		val item = itemStack.asSkyBlockItem ?: return

		if(item.id !in rareDrops) return
		item.timestamp?.let { if(it.elapsedSince() > 3.seconds) return } ?: return

		val uuid = item.uuid ?: return
		if(uuid in uuidCache) return
		uuidCache.add(uuid)

		val text = buildText {
			append(tr("nobaaddons.chat.rareDrop", "RARE DROP!").formatted(Formatting.GOLD, Formatting.BOLD))
			append(" ")
			append(itemStack.name)
		}

		ChatUtils.addMessage(text, prefix = false)
		SoundUtils.rareDropSound.play()
	}
}