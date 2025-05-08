package me.nobaboy.nobaaddons.commands

import dev.celestialfault.commander.annotations.AllowedRange
import dev.celestialfault.commander.annotations.Command
import dev.celestialfault.commander.annotations.Group
import me.nobaboy.nobaaddons.api.skyblock.PetAPI
import me.nobaboy.nobaaddons.commands.adapters.RarityHandler
import me.nobaboy.nobaaddons.core.Rarity
import me.nobaboy.nobaaddons.core.Skill
import me.nobaboy.nobaaddons.core.SkillData
import me.nobaboy.nobaaddons.utils.CollectionUtils.getOrLast
import me.nobaboy.nobaaddons.utils.NumberUtils.addSeparators
import me.nobaboy.nobaaddons.utils.NumberUtils.million
import me.nobaboy.nobaaddons.utils.NumberUtils.parseDoubleOrNull
import me.nobaboy.nobaaddons.utils.StringUtils.asDuration
import me.nobaboy.nobaaddons.utils.TextUtils.aqua
import me.nobaboy.nobaaddons.utils.TextUtils.buildText
import me.nobaboy.nobaaddons.utils.TextUtils.gold
import me.nobaboy.nobaaddons.utils.TextUtils.green
import me.nobaboy.nobaaddons.utils.TextUtils.red
import me.nobaboy.nobaaddons.utils.TextUtils.toText
import me.nobaboy.nobaaddons.utils.TextUtils.yellow
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import me.nobaboy.nobaaddons.utils.tr
import net.minecraft.text.Text
import net.minecraft.util.Colors
import org.apache.commons.lang3.StringUtils
import kotlin.math.floor
import kotlin.math.roundToLong

@Suppress("unused")
@Group("calculate", "calc")
object CalculateCommands {
	private val MISSING_REPO_DATA: Text = buildText {
		append(tr(
			"nobaaddons.command.calculate.missingRepo",
			"This command requires data which hasn't loaded correctly!"
		).red())
		append(" ")
		val command = "/nobaaddons repo update"
		append(tr(
			"nobaaddons.command.calculate.missingRepo.suggestUpdate",
			"Try running ${command.toText().aqua()}, or join the Discord for support if this persists."
		).yellow())
	}

	// a few notes on how exactly this works:
	//  - list times <1h are always 50 coins, no matter how long
	//  - the fee only increments for every full hour
	private val listTimeFee by lazy {
		buildList {
			add(20) // always starts at 20 coins for 1h
			repeat(6) { add(5) } // +5 coins for 6h, until 7h total for 50
			repeat(5) { add(10) } // +10 coins for 5h, until 12h total for 100
			repeat(11) { add(20) } // +20 coins for 11h, until 23h total for 320
			repeat(12) { add(30) } // +30 coins for 12h, until 35h total for 680
			repeat(13) { add(40) } // +40 coins for 13h, until 48h total for 1200
			// the fee then starts at 50 coins and increments by an extra 50 every 12h, up to a max of +200 flat
			// for every hour afterward
			repeat(37) {
				add((50 * (1 + floor(it.toDouble() / 12))).toInt())
			}
		}
	}

	private fun requiredXp(xp: Number): Text = tr("nobaaddons.command.calculate.requiredXp", "Required XP: ${xp.addSeparators().toText().aqua()}")

	@Command
	fun pet(
		rarity: @RarityHandler.Max(Rarity.MYTHIC) Rarity,
		startingLevel: @AllowedRange.Int(1, 200) Int,
		targetLevel: @AllowedRange.Int(1, 200) Int,
	) {
		val startingXp = PetAPI.xpFromLevel(startingLevel, rarity, maxLevel = 200)
		val targetXp = PetAPI.xpFromLevel(targetLevel, rarity, maxLevel = 200)
		ChatUtils.addMessage(requiredXp(targetXp - startingXp))
	}

	@Command
	fun skill(
		startingLevel: @AllowedRange.Int(0, 60) Int,
		targetLevel: @AllowedRange.Int(1, 60) Int,
		skill: Skill? = null,
	) {
		val constants = SkillData.INSTANCE
		if(constants == null) {
			ChatUtils.addMessage(MISSING_REPO_DATA)
			return
		}

		val startingXp = constants.calculateSkillXp(skill, startingLevel)
		val targetXp = constants.calculateSkillXp(skill, targetLevel)

		ChatUtils.addMessage(requiredXp(targetXp - startingXp))
	}

	// the Int.MAX_VALUE is required to avoid a reflection error(???)
	@Command
	fun cata(
		startingLevel: @AllowedRange.Int(0, Int.MAX_VALUE) Int,
		targetLevel: @AllowedRange.Int(1, Int.MAX_VALUE) Int,
	) {
		val constants = SkillData.INSTANCE
		if(constants == null) {
			ChatUtils.addMessage(MISSING_REPO_DATA)
			return
		}

		val startingXp = constants.calculateDungeonXp(startingLevel)
		val targetXp = constants.calculateDungeonXp(targetLevel)

		ChatUtils.addMessage(requiredXp(targetXp - startingXp))
	}

	@Command
	fun tax(amount: String, listTime: String? = null) {
		val listPrice: Long? = amount.parseDoubleOrNull()?.roundToLong()
		if(listPrice == null) {
			ChatUtils.addMessage(tr("nobaaddons.command.calculate.tax.invalid", "Invalid amount provided"))
			return
		}

		val fee: Long = when {
			listPrice < 10.million -> listPrice * 0.01
			listPrice < 100.million -> listPrice * 0.02
			else -> listPrice * 0.025
		}.roundToLong()

		val listTime: Int? = listTime?.let {
			if(StringUtils.isNumeric(it)) return@let it.toInt()
			it.asDuration()?.inWholeHours?.toInt()
		}?.coerceAtMost(14 * 24)
		val listTimeFee: Int? = listTime?.let {
			var fee = 0
			repeat(it) { fee += listTimeFee.getOrLast(it) }
			fee
		}

		val tax: Long = if(listPrice <= 1.million) 0 else (listPrice * 0.01).roundToLong()
		val finalProfit: Long = listPrice - fee - tax - (listTimeFee ?: 0)

		ChatUtils.addMessage(buildText {
			append(tr("nobaaddons.command.calculate.tax.starting", "Starting list price: ${listPrice.addSeparators().toText().yellow()}"))
			append("\n • ")
			append(tr("nobaaddons.command.calculate.tax.fee", "List fee: ${fee.addSeparators().toText().gold()}"))
			append("\n • ")
			if(listTimeFee != null) {
				append(tr("nobaaddons.command.calculate.tax.timeFee", "Time fee for $listTime hours: ${listTimeFee.addSeparators().toText().gold()}"))
				append("\n • ")
			}
			append(tr("nobaaddons.command.calculate.tax.taxes", "Claim taxes: ${tax.addSeparators().toText().gold()}"))
			append("\n • ")
			append(tr("nobaaddons.command.calculate.tax.finalProfit", "Final profit: ${finalProfit.addSeparators().toText().green()}"))
			withColor(Colors.CYAN)
		})
	}
}