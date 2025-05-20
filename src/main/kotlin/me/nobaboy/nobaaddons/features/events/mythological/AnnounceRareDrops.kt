package me.nobaboy.nobaaddons.features.events.mythological

import me.nobaboy.nobaaddons.api.skyblock.events.mythological.DianaAPI
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.events.impl.client.InventoryEvents
import me.nobaboy.nobaaddons.utils.TextUtils.buildText
import me.nobaboy.nobaaddons.utils.TimedSet
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import me.nobaboy.nobaaddons.utils.items.ItemUtils.asSkyBlockItem
import me.nobaboy.nobaaddons.utils.sound.SoundUtils
import me.nobaboy.nobaaddons.utils.tr
import me.owdding.ktmodules.Module
import net.minecraft.util.Formatting
import kotlin.time.Duration.Companion.seconds

@Module
object AnnounceRareDrops {
	private val config get() = NobaConfig.events.mythological
	private val enabled: Boolean get() = config.announceRareDrops && DianaAPI.isActive

	private val uuidCache = TimedSet<String>(10.seconds)

	private val rareDrops = listOf(
		"ANTIQUE_REMEDIES",
		"CROCHET_TIGER_PLUSHIE",
		"DWARF_TURTLE_SHELMET",
		"MINOS_RELIC"
	)

	init {
		InventoryEvents.SLOT_UPDATE.register(this::onSlotUpdate)
	}

	private fun onSlotUpdate(event: InventoryEvents.SlotUpdate) {
		if(!enabled) return

		val stack = event.stack
		val item = stack.asSkyBlockItem ?: return

		if(item.id !in rareDrops) return
		item.timestamp?.let { if(it.elapsedSince() > 3.seconds) return } ?: return

		val uuid = item.uuid ?: return
		if(uuid in uuidCache) return
		uuidCache.add(uuid)

		val text = buildText {
			append(tr("nobaaddons.chat.rareDrop", "RARE DROP!").formatted(Formatting.GOLD, Formatting.BOLD))
			append(" ")
			append(stack.name)
		}

		ChatUtils.addMessage(text, prefix = false)
		SoundUtils.rareDropSound.play()
	}
}