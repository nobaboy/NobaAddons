package me.nobaboy.nobaaddons.commands.debug

import me.nobaboy.nobaaddons.api.skyblock.PetAPI
import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI
import me.nobaboy.nobaaddons.commands.annotations.Command
import me.nobaboy.nobaaddons.commands.annotations.Group
import me.nobaboy.nobaaddons.commands.annotations.IntRange
import me.nobaboy.nobaaddons.commands.annotations.RootCommand
import me.nobaboy.nobaaddons.commands.debug.DebugCommands.dumpInfo
import me.nobaboy.nobaaddons.commands.impl.Context
import me.nobaboy.nobaaddons.core.Rarity
import me.nobaboy.nobaaddons.core.Rarity.Companion.rarityFormatted
import me.nobaboy.nobaaddons.utils.NumberUtils.addSeparators
import me.nobaboy.nobaaddons.utils.TextUtils.bold
import me.nobaboy.nobaaddons.utils.TextUtils.buildText
import me.nobaboy.nobaaddons.utils.TextUtils.toText
import net.minecraft.text.Text

@Suppress("unused")
@Group("pet")
object PetDebugCommands {
	@RootCommand
	fun root(ctx: Context) {
		if(!SkyBlockAPI.inSkyBlock) {
			ctx.source.sendError(Text.literal("You aren't in SkyBlock!"))
			return
		}

		val pet = PetAPI.currentPet
		if(pet == null) {
			ctx.source.sendError(Text.literal("You don't have a pet equipped"))
			return
		}

		ctx.dumpInfo(
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

	@Command
	fun level(ctx: Context, xp: Double, rarity: Rarity, maxLevel: @IntRange(100, 200) Int = 100) {
		val level = PetAPI.levelFromXp(xp, rarity, maxLevel)
		ctx.source.sendFeedback("$rarity XP ${xp.addSeparators()} -> $level".toText())
	}

	@Command
	fun xp(ctx: Context, level: @IntRange(1, 100) Int, rarity: Rarity) {
		val xp = PetAPI.xpFromLevel(level, rarity, 200).addSeparators()
		ctx.source.sendFeedback("$rarity LVL $level -> $xp".toText())
	}
}