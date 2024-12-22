package me.nobaboy.nobaaddons.commands.debug

import com.mojang.brigadier.context.CommandContext
import me.nobaboy.nobaaddons.commands.debug.DebugCommands.dumpInfo
import me.nobaboy.nobaaddons.commands.internal.Command
import me.nobaboy.nobaaddons.commands.internal.Group
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.items.ItemUtils.isSkyBlockItem
import me.nobaboy.nobaaddons.utils.items.ItemUtils.skyblockItem
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.component.DataComponentTypes
import net.minecraft.text.Text

@Suppress("unused")
object ItemDebugCommands : Group("item", executeRoot = true) {
	override fun execute(ctx: CommandContext<FabricClientCommandSource>): Int {
		val item = MCUtils.player!!.mainHandStack
		if(item.isEmpty || !item.isSkyBlockItem) {
			ctx.source.sendError(Text.literal("You aren't holding a valid SkyBlock item"))
			return 0
		}
		val itemData = item.skyblockItem()
		ctx.dumpInfo(
			"Item ID" to itemData.id,
			"UUID" to itemData.uuid,
			"Created" to itemData.timestamp?.elapsedSince(),
			"Reforge" to itemData.reforge,
			"Rarity" to itemData.rarity,
			"Recombobulated" to itemData.recombobulated,
			"Stars" to itemData.stars,
			"Enchants" to itemData.enchantments,
			"Gemstones" to itemData.gemstones,
			"Power scroll" to itemData.powerScroll,
			"Donated to Museum" to itemData.donatedToMuseum,
		)
		return 0
	}

	val dumpNbt = Command.command("nbt") {
		executes {
			val item = MCUtils.player!!.mainHandStack
			if(item.isEmpty) {
				source.sendError(Text.literal("You aren't holding an item"))
				return@executes
			}
			println(item.get(DataComponentTypes.CUSTOM_DATA))
			source.sendFeedback(Text.literal("Dumped item NBT to game logs"))
		}
	}

	val dumpLore = Command.command("lore") {
		executes {
			val item = MCUtils.player!!.mainHandStack
			if(item.isEmpty || !item.contains(DataComponentTypes.LORE)) {
				source.sendError(Text.literal("You aren't holding an item with lore"))
				return@executes
			}
			println(item.get(DataComponentTypes.LORE)!!.lines)
			source.sendFeedback(Text.literal("Dumped item lore to game logs"))
		}
	}
}