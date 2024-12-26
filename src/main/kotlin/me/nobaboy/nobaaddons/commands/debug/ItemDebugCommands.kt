package me.nobaboy.nobaaddons.commands.debug

import me.nobaboy.nobaaddons.commands.debug.DebugCommands.dumpInfo
import me.nobaboy.nobaaddons.commands.internal.Command
import me.nobaboy.nobaaddons.commands.internal.Group
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.items.ItemUtils.isSkyBlockItem
import me.nobaboy.nobaaddons.utils.items.ItemUtils.skyblockItem
import net.minecraft.component.DataComponentTypes
import net.minecraft.text.Text

@Suppress("unused")
object ItemDebugCommands : Group("item") {
	override val root = RootCommand {
		val item = MCUtils.player!!.mainHandStack
		if(item.isEmpty || !item.isSkyBlockItem) {
			it.source.sendError(Text.literal("You aren't holding a valid SkyBlock item"))
			return@RootCommand
		}

		val itemData = item.skyblockItem()
		it.dumpInfo(
			"Item ID" to itemData.id,
			"UUID" to itemData.uuid,
			"Created" to itemData.timestamp?.elapsedSince(),
			"Reforge" to itemData.reforge,
			"Rarity" to itemData.rarity,
			"Recombobulated" to itemData.recombobulated,
			"Stars" to itemData.stars,
			"Enchants" to itemData.enchantments,
			"Runes" to itemData.runes,
			"Gemstones" to itemData.gemstones,
			"Power scroll" to itemData.powerScroll,
			"Potion" to itemData.potion,
			"Potion level" to itemData.potionLevel,
			"Potion effects" to itemData.effects,
			"Donated to Museum" to itemData.donatedToMuseum,
		)
	}

	val dumpNbt = Command("nbt") {
		val item = MCUtils.player!!.mainHandStack
		if(item.isEmpty) {
			it.source.sendError(Text.literal("You aren't holding an item"))
			return@Command
		}

		println(item.get(DataComponentTypes.CUSTOM_DATA))
		it.source.sendFeedback(Text.literal("Dumped item NBT to game logs"))
	}

	val dumpLore = Command("lore") {
		val item = MCUtils.player!!.mainHandStack
		if(item.isEmpty || !item.contains(DataComponentTypes.LORE)) {
			it.source.sendError(Text.literal("You aren't holding an item with lore"))
			return@Command
		}

		println(item.get(DataComponentTypes.LORE)!!.lines)
		it.source.sendFeedback(Text.literal("Dumped item lore to game logs"))
	}
}