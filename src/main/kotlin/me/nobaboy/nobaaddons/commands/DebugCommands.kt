package me.nobaboy.nobaaddons.commands

import com.mojang.brigadier.context.CommandContext
import me.nobaboy.nobaaddons.api.DebugAPI
import me.nobaboy.nobaaddons.api.PartyAPI
import me.nobaboy.nobaaddons.api.skyblock.MayorAPI
import me.nobaboy.nobaaddons.api.skyblock.PetAPI
import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI
import me.nobaboy.nobaaddons.commands.internal.Command
import me.nobaboy.nobaaddons.commands.internal.Group
import me.nobaboy.nobaaddons.core.mayor.Mayor
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.TextUtils.buildText
import me.nobaboy.nobaaddons.utils.TextUtils.toText
import me.nobaboy.nobaaddons.utils.items.ItemUtils.isSkyBlockItem
import me.nobaboy.nobaaddons.utils.items.ItemUtils.skyblockItem
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.component.DataComponentTypes
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import kotlin.jvm.optionals.getOrNull

@Suppress("unused")
object DebugCommands : Group("debug") {
	private fun MutableText.data(vararg items: Pair<String, Any?>) {
		items.forEach {
			append(Text.literal("${it.first}: ").formatted(Formatting.BLUE))
			append(Text.literal(it.second.toString()).formatted(Formatting.AQUA))
			append("\n")
		}
	}

	private fun CommandContext<FabricClientCommandSource>.dumpInfo(vararg items: Pair<String, Any?>) {
		val text = buildText {
			append("-".repeat(20).toText().formatted(Formatting.GRAY))
			append("\n")
			data(*items)
			append("-".repeat(20).toText().formatted(Formatting.GRAY))
		}
		source.sendFeedback(text)
	}

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

	val mayor = Command.command("mayor") {
		executes {
			val mayor = MayorAPI.currentMayor
			val minister = MayorAPI.currentMinister

			if(mayor == Mayor.UNKNOWN && minister == Mayor.UNKNOWN) {
				source.sendError(Text.literal("Current Mayor and Minister are still unknown"))
				return@executes
			}

			dumpInfo(
				"Current Mayor" to mayor.mayorName,
				"Mayor Perks" to mayor.activePerks,
				"Current Minister" to minister.mayorName,
				"Minister Perk" to minister.activePerks,
			)
		}
	}

	val pet = Command.command("pet") {
		executes {
			val pet = PetAPI.currentPet
			if(pet == null) {
				source.sendError(Text.literal("You don't have a pet equipped"))
				return@executes
			}

			dumpInfo(
				"Name" to pet.name,
				"Pet ID" to pet.id,
				"Level" to pet.level,
				"XP" to pet.xp,
				"Rarity" to pet.rarity,
				"Held Item" to pet.heldItem,
				"UUID" to pet.uuid
			)
		}
	}

	val sounds = Command.command("sounds") {
		enabled = DebugAPI.isAwtAvailable

		executes {
			DebugAPI.openSoundDebugMenu()
		}
	}

	val location = Command.command("location") {
		executes {
			val location = DebugAPI.lastLocationPacket
			dumpInfo(
				"Server" to location.serverName,
				"Type" to location.serverType.getOrNull(),
				"Lobby" to location.lobbyName.getOrNull(),
				"Mode" to location.mode.getOrNull(),
				"Map" to location.map.getOrNull(),
				"Detected Island" to SkyBlockAPI.currentIsland,
			)
		}
	}
}