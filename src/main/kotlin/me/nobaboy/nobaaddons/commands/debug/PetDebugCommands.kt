package me.nobaboy.nobaaddons.commands.debug

import dev.celestialfault.commander.annotations.AllowedRange
import dev.celestialfault.commander.annotations.Command
import dev.celestialfault.commander.annotations.Group
import dev.celestialfault.commander.annotations.RootCommand
import me.nobaboy.nobaaddons.api.skyblock.PetAPI
import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI
import me.nobaboy.nobaaddons.commands.debug.DebugCommands.dumpInfo
import me.nobaboy.nobaaddons.commands.impl.Context
import me.nobaboy.nobaaddons.core.Rarity
import me.nobaboy.nobaaddons.core.Rarity.Companion.rarityFormatted
import me.nobaboy.nobaaddons.utils.NumberUtils.addSeparators
import me.nobaboy.nobaaddons.utils.mc.TextUtils.bold
import me.nobaboy.nobaaddons.utils.mc.TextUtils.buildText
import me.nobaboy.nobaaddons.utils.mc.TextUtils.toText
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
	fun level(ctx: Context, xp: Double, rarity: Rarity, maxLevel: @AllowedRange.Int(100, 200) Int = 100) {
		val level = PetAPI.levelFromXp(xp, rarity, maxLevel)
		ctx.source.sendFeedback("$rarity XP ${xp.addSeparators()} -> $level".toText())
	}

	@Command
	fun xp(ctx: Context, level: @AllowedRange.Int(100, 200) Int, rarity: Rarity) {
		val xp = PetAPI.xpFromLevel(level, rarity, 200).addSeparators()
		ctx.source.sendFeedback("$rarity LVL $level -> $xp".toText())
	}
}