package me.nobaboy.nobaaddons.commands.debug

import com.mojang.brigadier.arguments.DoubleArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.context.CommandContext
import me.nobaboy.nobaaddons.api.skyblock.PetAPI
import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI
import me.nobaboy.nobaaddons.commands.debug.DebugCommands.dumpInfo
import me.nobaboy.nobaaddons.commands.internal.Command
import me.nobaboy.nobaaddons.commands.internal.Group
import me.nobaboy.nobaaddons.core.ItemRarity
import me.nobaboy.nobaaddons.utils.NumberUtils.addSeparators
import me.nobaboy.nobaaddons.utils.TextUtils.bold
import me.nobaboy.nobaaddons.utils.TextUtils.buildText
import me.nobaboy.nobaaddons.utils.TextUtils.formatted
import me.nobaboy.nobaaddons.utils.TextUtils.toText
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.text.Text
import net.minecraft.util.Formatting

@Suppress("unused")
object PetDebugCommands : Group("pet", executeRoot = true) {
	override fun execute(ctx: CommandContext<FabricClientCommandSource>): Int {
		if(!SkyBlockAPI.inSkyBlock) {
			ctx.source.sendError(Text.literal("You aren't in SkyBlock!"))
			return 0
		}

		val pet = PetAPI.currentPet
		if(pet == null) {
			ctx.source.sendError(Text.literal("You don't have a pet equipped"))
			return 0
		}

		ctx.dumpInfo(
			"Pet" to buildText {
				append(pet.rarity.name.toText().bold())
				append(" ")
				append(pet.name.formatted(pet.rarity.formatting ?: Formatting.WHITE))
			},
			"ID" to pet.id,
			"XP" to pet.xp,
			"Level" to pet.level,
			"Held Item" to pet.heldItem,
			"UUID" to pet.uuid,
		)

		return 0
	}

	val level = Command.command("level") {
		buildCommand {
			it.then(ClientCommandManager.argument("xp", DoubleArgumentType.doubleArg())
				.then(ClientCommandManager.argument("rarity", ItemRarity.ItemRarityArgumentType)
					.then(ClientCommandManager.argument("max", IntegerArgumentType.integer(100, 200))
						.executes(this::execute))
					.executes(this::execute)))
		}

		executes {
			val xp = DoubleArgumentType.getDouble(this, "xp")
			val rarity = ItemRarity.ItemRarityArgumentType.getItemRarity(this, "rarity")
			val maxLevel = runCatching { IntegerArgumentType.getInteger(this, "max") }.getOrDefault(100)
			val level = PetAPI.levelFromXp(xp, rarity, maxLevel)
			source.sendFeedback("$rarity XP ${xp.addSeparators()} -> $level".toText())
		}
	}

	val xp = Command.command("xp") {
		buildCommand {
			it.then(ClientCommandManager.argument("level", IntegerArgumentType.integer(1, 100))
				.then(ClientCommandManager.argument("rarity", ItemRarity.ItemRarityArgumentType)
					.executes(this::execute)))
		}

		executes {
			val level = IntegerArgumentType.getInteger(this, "level")
			val rarity = ItemRarity.ItemRarityArgumentType.getItemRarity(this, "rarity")
			val xp = PetAPI.xpFromLevel(level, rarity, 200).addSeparators()
			source.sendFeedback("$rarity LVL $level -> $xp".toText())
		}
	}
}