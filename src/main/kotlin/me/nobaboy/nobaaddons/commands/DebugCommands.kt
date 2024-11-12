package me.nobaboy.nobaaddons.commands

import com.mojang.brigadier.context.CommandContext
import me.nobaboy.nobaaddons.api.PartyAPI
import me.nobaboy.nobaaddons.commands.internal.Command
import me.nobaboy.nobaaddons.commands.internal.Group
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.TextUtils.buildText
import me.nobaboy.nobaaddons.utils.TextUtils.toText
import me.nobaboy.nobaaddons.utils.items.ItemUtils.isSkyBlockItem
import me.nobaboy.nobaaddons.utils.items.ItemUtils.skyblockItem
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.component.DataComponentTypes
import net.minecraft.text.Text
import net.minecraft.util.Formatting

@Suppress("unused")
object DebugCommands : Group("debug") {
	val party = Command.command("party") {
		executes {
			PartyAPI.listMembers()
		}
	}

	object Item : Group("item", executeRoot = true) {
		override fun execute(ctx: CommandContext<FabricClientCommandSource>): Int {
			val item = MCUtils.player!!.mainHandStack
			if(item.isEmpty || !item.isSkyBlockItem) {
				ctx.source.sendError(Text.literal("You aren't holding a valid SkyBlock item"))
				return 0
			}
			val itemData = item.skyblockItem()
			ctx.source.sendFeedback(buildText {
				fun data(vararg items: Pair<String, Any?>) {
					items.forEach {
						append(Text.literal("${it.first}: ").formatted(Formatting.BLUE))
						append(Text.literal(it.second.toString()).formatted(Formatting.AQUA))
						append("\n")
					}
				}

				append("-".repeat(20).toText().formatted(Formatting.GRAY))
				append("\n")
				append(item.name)
				append("\n\n")
				data(
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
				append("-".repeat(20).toText().formatted(Formatting.GRAY))
			})
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
}