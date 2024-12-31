package me.nobaboy.nobaaddons.commands.debug

import com.mojang.brigadier.arguments.DoubleArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import me.nobaboy.nobaaddons.api.skyblock.PetAPI
import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI
import me.nobaboy.nobaaddons.commands.debug.DebugCommands.dumpInfo
import me.nobaboy.nobaaddons.commands.internal.Command
import me.nobaboy.nobaaddons.commands.internal.Group
import me.nobaboy.nobaaddons.core.Rarity
import me.nobaboy.nobaaddons.core.Rarity.Companion.rarityFormatted
import me.nobaboy.nobaaddons.utils.NumberUtils.addSeparators
import me.nobaboy.nobaaddons.utils.TextUtils.bold
import me.nobaboy.nobaaddons.utils.TextUtils.buildText
import me.nobaboy.nobaaddons.utils.TextUtils.toText
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.minecraft.text.Text

@Suppress("unused")
object PetDebugCommands : Group("pet") {
	override val root = RootCommand {
		if(!SkyBlockAPI.inSkyBlock) {
			it.source.sendError(Text.literal("You aren't in SkyBlock!"))
			return@RootCommand
		}

		val pet = PetAPI.currentPet
		if(pet == null) {
			it.source.sendError(Text.literal("You don't have a pet equipped"))
			return@RootCommand
		}

		it.dumpInfo(
			"Pet" to buildText {
				append(pet.rarity.name.toText().bold())
				append(" ")
				append(pet.name.toText().rarityFormatted(pet.rarity))
			},
			"ID" to pet.id,
			"XP" to pet.xp.addSeparators(),
			"Level" to pet.level,
			"Held Item" to pet.heldItem,
			"UUID" to pet.uuid,
		)
	}

	val level = Command(
		"level",
		builder = {
			it.then(ClientCommandManager.argument("xp", DoubleArgumentType.doubleArg())
				.then(ClientCommandManager.argument("rarity", Rarity.RarityArgumentType)
					.then(ClientCommandManager.argument("max", IntegerArgumentType.integer(100, 200))
						.executes(this::execute))
					.executes(this::execute)))
		}
	) {
		val xp = DoubleArgumentType.getDouble(it, "xp")
		val rarity = Rarity.RarityArgumentType.getItemRarity(it, "rarity")
		val maxLevel = runCatching { IntegerArgumentType.getInteger(it, "max") }.getOrDefault(100)
		val level = PetAPI.levelFromXp(xp, rarity, maxLevel)
		it.source.sendFeedback("$rarity XP ${xp.addSeparators()} -> $level".toText())
	}

	val xp = Command(
		"xp",
		builder = {
			it.then(ClientCommandManager.argument("level", IntegerArgumentType.integer(1, 100))
				.then(ClientCommandManager.argument("rarity", Rarity.RarityArgumentType)
					.executes(this::execute)))
		}
	) {
		val level = IntegerArgumentType.getInteger(it, "level")
		val rarity = Rarity.RarityArgumentType.getItemRarity(it, "rarity")
		val xp = PetAPI.xpFromLevel(level, rarity, 200).addSeparators()
		it.source.sendFeedback("$rarity LVL $level -> $xp".toText())
	}
}