package me.nobaboy.nobaaddons.commands

import dev.celestialfault.commander.annotations.AllowedRange
import dev.celestialfault.commander.annotations.Command
import dev.celestialfault.commander.annotations.Group
import me.nobaboy.nobaaddons.api.skyblock.PetAPI
import me.nobaboy.nobaaddons.core.Rarity
import me.nobaboy.nobaaddons.core.Skill
import me.nobaboy.nobaaddons.core.SkillData
import me.nobaboy.nobaaddons.utils.NumberUtils.addSeparators
import me.nobaboy.nobaaddons.utils.NumberUtils.million
import me.nobaboy.nobaaddons.utils.NumberUtils.parseDoubleOrNull
import me.nobaboy.nobaaddons.utils.TextUtils.aqua
import me.nobaboy.nobaaddons.utils.TextUtils.buildText
import me.nobaboy.nobaaddons.utils.TextUtils.gray
import me.nobaboy.nobaaddons.utils.TextUtils.green
import me.nobaboy.nobaaddons.utils.TextUtils.red
import me.nobaboy.nobaaddons.utils.TextUtils.toText
import me.nobaboy.nobaaddons.utils.TextUtils.yellow
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import me.nobaboy.nobaaddons.utils.tr
import net.minecraft.text.Text
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

	private fun requiredXp(xp: Number) = tr("nobaaddons.command.calculate.requiredXp", "Required XP: ${xp.addSeparators().toText().aqua()}")

	@Command
	fun pet(
		rarity: Rarity,
		startingLevel: @AllowedRange.Int(1, 200) Int,
		targetLevel: @AllowedRange.Int(1, 200) Int,
	) {
		val startingXp = PetAPI.xpFromLevel(startingLevel, rarity, maxLevel = 200)
		val targetXp = PetAPI.xpFromLevel(targetLevel, rarity, maxLevel = 200)
		val difference = targetXp - startingXp
		ChatUtils.addMessage(tr("nobaaddons.command.calculate.pet", "${difference.addSeparators()} XP is required to level a ${rarity.displayName} pet from $startingLevel to $targetLevel"))
	}

	@Command
	fun skill(startingLevel: @AllowedRange.Int(0, 60) Int, targetLevel: @AllowedRange.Int(1, 60) Int, skill: Skill? = null) {
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
	fun cata(startingLevel: @AllowedRange.Int(0, Int.MAX_VALUE) Int, targetLevel: @AllowedRange.Int(1, Int.MAX_VALUE) Int) {
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
	fun tax(amount: String) {
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

		val tax: Long = if(listPrice < 1.million) 0 else (listPrice * 0.01).roundToLong()
		val finalProfit: Long = listPrice - fee - tax

		ChatUtils.addMessage(buildText {
			append(listPrice.addSeparators().toText().aqua())
			append(" - ")
			append(fee.addSeparators().toText().yellow())
			append(" ")
			append(tr("nobaaddons.command.calculate.tax.fee", "(list fee)").gray())
			append(" - ")
			append(tax.addSeparators().toText().yellow())
			append(" ")
			append(tr("nobaaddons.command.calculate.tax.taxes", "(taxes)").gray())
			append(" = ")
			append(finalProfit.addSeparators().toText().green())
			append(" ")
			append(tr("nobaaddons.command.calculate.tax.finalProfit", "final profit").gray())
		})
	}
}