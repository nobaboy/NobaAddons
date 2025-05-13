package me.nobaboy.nobaaddons.features.inventory

//? if >=1.21.5 {
/*import me.nobaboy.nobaaddons.mixins.accessors.PlayerInventoryAccessor
import net.minecraft.entity.EquipmentSlot
*///?}

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap
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
import kotlin.time.Duration.Companion.seconds

object ItemPickupLog {
	private val MERCHANT_COUNT = Regex("x\\d+")
	private const val SKYBLOCK_MENU_SLOT = 8

	private val config get() = NobaConfig.inventory.itemPickupLog
	private val enabled: Boolean get() = config.enabled && SkyBlockAPI.inSkyBlock

	private val items = Object2ObjectArrayMap<Text, Int>()
	private val itemLog = Object2ObjectArrayMap<Text, ItemEntry>()

	private var suppressTime = Timestamp.distantPast()

	init {
		SkyBlockEvents.ISLAND_CHANGE.register { reset() }
		TickEvents.TICK.register(this::onTick)
		UIManager.add(PickupLogHudElement)
	}

	private fun onTick(event: TickEvents.Tick) {
		if(!enabled) return

		itemLog.values.removeIf { it.expired }

		if(event.client.currentScreen != null) return
		val player = event.client.player ?: return

		val newItems = player.inventory.nameToCount()
		val oldItems = mutableMapOf<Text, Int>().apply { putAll(items) }
		items.clear()

		newItems.forEach { (name, count) ->
			items[name] = items.getOrDefault(name, 0) + count
		}

		// ignore item changes for ~2s when switching servers, as your inventory is
		// initially empty when loading in
		if(suppressTime.elapsedSince() < 2.seconds) return
		detectItemChanges(items, oldItems)
	}

	// TODO either remove current and just use items straight out or suppress this
	private fun detectItemChanges(current: Map<Text, Int>, previous: Map<Text, Int>) {
		val names = current.keys + previous.keys

		for(name in names) {
			val delta = (current[name] ?: 0) - (previous[name] ?: 0)
			if(delta == 0) continue

			val entry = itemLog.getOrPut(name) { ItemEntry() }
			if(delta > 0) entry.added += delta else entry.removed += -delta
			entry.timestamp = Timestamp.now()
		}
	}

	private fun PlayerInventory.nameToCount(): Map<Text, Int> = buildMap {
		//? if >=1.21.5 {
		/*val main = (this@nameToCount as PlayerInventoryAccessor).main
		val equipment = (this@nameToCount as PlayerInventoryAccessor).equipment
		*///?}

		main.forEachIndexed { slot, stack ->
			if(slot == SKYBLOCK_MENU_SLOT || stack.isEmpty) return@forEachIndexed

			val name = stack.name.removeMerchantCount()
			merge(name, stack.count, Int::plus)
		}

		//? if >=1.21.5 {
		/*equipment.get(EquipmentSlot.OFFHAND)
		*///?} else {
		offHand.firstOrNull()
		//?}
			?.takeIf { !it.isEmpty }
			?.let { merge(it.name, it.count, Int::plus) }
	}

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

	private fun compileCompactLines(
		added: Map<Text, ItemEntry>,
		removed: Map<Text, ItemEntry>,
	): List<Text> = buildList {
		val names = added.keys + removed.keys

		names.forEach { name ->
			val delta = (added[name]?.added ?: 0) - (removed[name]?.removed ?: 0)
			if(delta == 0) return@forEach

			add(buildText {
				val symbol = if(delta > 0) "+" else ""
				literal("$symbol${delta.addSeparators()}x ") {
					if(delta > 0) green() else red()
				}
				append(name)
			})
		}
	}

	private fun compileSplitLines(
		added: Map<Text, ItemEntry>,
		removed: Map<Text, ItemEntry>,
	): List<Text> = buildList {
		val names = added.keys + removed.keys

		names.forEach { name ->
			added[name]?.let { entry ->
				add(buildText {
					literal("+${entry.added.addSeparators()}x ") { green() }
					append(name)
				})
			}

			removed[name]?.let { entry ->
				add(buildText {
					literal("-${entry.removed.addSeparators()}x ") { red() }
					append(name)
				})
			}
		}
	}

	private fun reset() {
		items.clear()
		itemLog.clear()
		suppressTime = Timestamp.now()
	}

	private object PickupLogHudElement : TextHudElement(UISettings.itemPickupLog) {
		override val name: Text = tr("nobaaddons.ui.itemPickupLog", "Item Pickup Log")
		override val size: Pair<Int, Int> = 125 to 175
		override val enabled: Boolean get() = config.enabled
		override val color: Int = 0xFFFFFF
		override val maxScale: Float = 1f

		override fun renderText(context: DrawContext) {
			val itemLog = itemLog.clone()

			val added = itemLog.filterValues { it.added > 0 }
			val removed = itemLog.filterValues { it.removed > 0 }

			val lines = if(config.compactLines) {
				compileCompactLines(added, removed)
			} else {
				compileSplitLines(added, removed)
			}

			renderLines(context, lines)
		}
	}

	private data class ItemEntry(
		var added: Int = 0,
		var removed: Int = 0,
		var timestamp: Timestamp = Timestamp.now(),
	) {
		val expired: Boolean
			get() = timestamp.elapsedSince() > config.timeoutSeconds.seconds
	}
}