package me.nobaboy.nobaaddons.features.inventory

//? if >=1.21.5 {
/*import me.nobaboy.nobaaddons.mixins.accessors.PlayerInventoryAccessor
*///?}

import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.config.UISettings
import me.nobaboy.nobaaddons.events.impl.client.TickEvents
import me.nobaboy.nobaaddons.events.impl.skyblock.SkyBlockEvents
import me.nobaboy.nobaaddons.ui.TextHudElement
import me.nobaboy.nobaaddons.ui.UIManager
import me.nobaboy.nobaaddons.utils.NumberUtils.addSeparators
import me.nobaboy.nobaaddons.utils.TextUtils.buildLiteral
import me.nobaboy.nobaaddons.utils.TextUtils.buildText
import me.nobaboy.nobaaddons.utils.TextUtils.green
import me.nobaboy.nobaaddons.utils.TextUtils.literal
import me.nobaboy.nobaaddons.utils.TextUtils.red
import me.nobaboy.nobaaddons.utils.Timestamp
import me.nobaboy.nobaaddons.utils.tr
import net.minecraft.client.gui.DrawContext
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import kotlin.math.abs
import kotlin.time.Duration.Companion.seconds

object ItemPickupLog {
	private val MERCHANT_COUNT = Regex("x\\d+")
	private const val SKYBLOCK_MENU_SLOT = 8

	private val config get() = NobaConfig.inventory.itemPickupLog
	private val enabled: Boolean get() = config.enabled && SkyBlockAPI.inSkyBlock

	private val items = mutableMapOf<Text, Int>()
	private var suppressTime = Timestamp.distantPast()

	private val addedItems = mutableMapOf<Text, ItemEntry>()
	private val removedItems = mutableMapOf<Text, ItemEntry>()

	init {
		SkyBlockEvents.ISLAND_CHANGE.register { reset() }
		TickEvents.TICK.register(this::onTick)
		UIManager.add(PickupLogHudElement)
	}

	private fun onTick(event: TickEvents.Tick) {
		if(!enabled) return
		if(event.client.currentScreen != null) return

		val player = event.client.player ?: return

		val newItems = player.inventory.nameToCount()
		val oldItems = mutableMapOf<Text, Int>().apply { putAll(items) }
		items.clear()

		newItems.forEach { (name, count) ->
			items[name] = items.getOrDefault(name, 0) + count
		}

		// this check is here solely to update items because it's empty to start with
		if(suppressTime.elapsedSince() < 2.seconds) return
		detectItemChanges(items, oldItems)
	}

	// TODO either remove current and just use items straight out or suppress this
	private fun detectItemChanges(current: Map<Text, Int>, previous: Map<Text, Int>) {
		val names = current.keys + previous.keys

		for(name in names) {
			val delta = (current[name] ?: 0) - (previous[name] ?: 0)
			if(delta == 0) continue

			val map = if(delta > 0) addedItems else removedItems
			val previousChange = map[name]?.change ?: 0
			map[name] = ItemEntry(previousChange + abs(delta))
		}
	}

	private fun PlayerInventory.nameToCount(): Map<Text, Int> = buildMap {
		//? if >=1.21.5 {
		/*val main = (this@nameToCount as PlayerInventoryAccessor).main
		*///?}

		main.forEachIndexed { slot, stack ->
			if(slot == SKYBLOCK_MENU_SLOT || stack.isEmpty) return@forEachIndexed

			val name = stack.name.removeMerchantCount()
			merge(name, stack.count, Int::plus)
		}

		// TODO fix for 1.21.5 and remove merchant count from name just in case
//		offHand.firstOrNull()?.let { merge(name, it.count, Int::plus) }
	}

	// TODO maybe clean this up, can't get rid of it cuz we need to remove the count either way
	private fun Text.removeMerchantCount(): Text {
		if(siblings.size <= 1) return this
		val last = siblings.last()

		// celeste — Today at 01:19
		// i have reworded this comment like 10 times now
		// i cannot figure out a way to properly express how much i hate having to do this
		if(last.string.matches(MERCHANT_COUNT) && last.style.color?.rgb == Formatting.DARK_GRAY.colorValue!!) {
			val copy = copy()
			copy.siblings.removeLast()
			val name = copy.siblings.removeLast()
			val content = name.string.removeSuffix(" ")
			copy.append(buildLiteral(content) { style = name.style })
			return copy
		}

		return this
	}

	// TODO wtf is this, split into 2 functions and call whichever based on config.compactLines
	private fun buildDisplayLines(): List<Text> {
		addedItems.values.removeIf { it.expired }
		removedItems.values.removeIf { it.expired }

		return buildList {
			if(config.compactLines) {
				val names = (addedItems.keys + removedItems.keys).toSet()

				for(name in names) {
					val added = addedItems[name]?.change ?: 0
					val removed = removedItems[name]?.change ?: 0

					val delta = added - removed
					if(delta == 0) continue

					add(buildText {
						literal(if(delta > 0) "+${delta.addSeparators()}x " else "-${(-delta).addSeparators()}x ") {
							if(delta > 0) green() else red()
						}
						append(name)
					})
				}
			} else {
				for((name, addEntry) in addedItems) {
					add(buildText {
						literal("+${addEntry.change.addSeparators()}x ") { green() }
						append(name)
					})

					removedItems[name]?.let { removeEntry ->
						add(buildText {
							literal("-${removeEntry.change.addSeparators()}x ") { red() }
							append(name)
						})
					}
				}

				for((name, removeEntry) in removedItems) {
					if(name !in addedItems) {
						add(buildText {
							literal("-${removeEntry.change.addSeparators()}x ") { red() }
							append(name)
						})
					}
				}
			}
		}
	}


	private fun reset() {
		items.clear()
		addedItems.clear()
		removedItems.clear()
		suppressTime = Timestamp.now()
	}

	private object PickupLogHudElement : TextHudElement(UISettings.itemPickupLog) {
		override val name: Text = tr("nobaaddons.ui.itemPickupLog", "Item Pickup Log")
		override val size: Pair<Int, Int> = 125 to 175
		override val enabled: Boolean get() = config.enabled
		override val color: Int = 0xFFFFFF
		override val maxScale: Float = 1f

		override fun renderText(context: DrawContext) {
			renderLines(context, buildDisplayLines())
		}
	}

	private data class ItemEntry(
		var change: Int = 0,
		var timestamp: Timestamp = Timestamp.now(),
	) {
		val expired: Boolean
			get() = timestamp.elapsedSince() > config.timeoutSeconds.seconds
	}
}