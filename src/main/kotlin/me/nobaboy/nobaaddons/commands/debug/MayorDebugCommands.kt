package me.nobaboy.nobaaddons.commands.debug

import dev.celestialfault.commander.annotations.Command
import dev.celestialfault.commander.annotations.Group
import dev.celestialfault.commander.annotations.RootCommand
import me.nobaboy.nobaaddons.api.skyblock.MayorAPI
import me.nobaboy.nobaaddons.commands.debug.DebugCommands.dumpInfo
import me.nobaboy.nobaaddons.commands.impl.CommandUtil
import me.nobaboy.nobaaddons.commands.impl.CommandUtil.addHandler
import me.nobaboy.nobaaddons.commands.impl.Context
import me.nobaboy.nobaaddons.core.mayor.Mayor
import me.nobaboy.nobaaddons.core.mayor.MayorPerk
import me.nobaboy.nobaaddons.utils.JavaUtils
import me.nobaboy.nobaaddons.utils.TextUtils.buildLiteral
import me.nobaboy.nobaaddons.utils.TextUtils.gray
import me.nobaboy.nobaaddons.utils.TextUtils.hoverText
import me.nobaboy.nobaaddons.utils.TextUtils.toText
import me.nobaboy.nobaaddons.utils.TextUtils.underline
import me.nobaboy.nobaaddons.utils.Timestamp
import me.nobaboy.nobaaddons.utils.annotations.UntranslatedMessage
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import net.minecraft.text.Text
import net.minecraft.text.Texts

@OptIn(UntranslatedMessage::class)
@Suppress("unused")
@Group("mayor")
object MayorDebugCommands {
	init {
		CommandUtil.commander.addHandler(JavaUtils.enumArgument(Mayor::class.java))
		CommandUtil.commander.addHandler(JavaUtils.enumArgument(MayorPerk::class.java))
	}

	@RootCommand
	fun currentMayor(ctx: Context) {
		val mayor = MayorAPI.currentMayor
		val minister = MayorAPI.currentMinister

		if(mayor.mayor == Mayor.UNKNOWN && minister.mayor == Mayor.UNKNOWN) {
			ctx.source.sendError(Text.literal("Current Mayor and Minister are still unknown"))
			return
		}

		ctx.dumpInfo(
			"Current Mayor" to buildLiteral(mayor.displayName) {
				if(mayor.perks.isNotEmpty()) {
					hoverText(Texts.join(mayor.perks.map { it.toString().toText().gray() }, Text.literal("\n\n")))
					underline()
				}
				gray()
			},

			"Current Minister" to buildLiteral(minister.displayName) {
				if(minister.perks.isNotEmpty()) {
					hoverText(Texts.join(minister.perks.map { it.toString().toText().gray() }, Text.literal("\n\n")))
					underline()
				}
				gray()
			}
		)
	}

	@Command
	fun reset() {
		MayorAPI.suppressAutoUpdate = false
		ChatUtils.addMessage("Reset currently forced mayor")
	}

	@Command
	fun assumeMayor(mayor: Mayor) {
		MayorAPI.suppressAutoUpdate = true
		MayorAPI.currentMayor = mayor.withAll()
		ChatUtils.addMessage("Assuming current mayor is $mayor with all perks active")
	}

	@Command
	fun assumeMinister(mayor: Mayor, perk: MayorPerk) {
		MayorAPI.suppressAutoUpdate = true
		MayorAPI.currentMinister = MayorAPI.ActiveMayor(mayor, listOf(perk))
		ChatUtils.addMessage("Assuming current minister is $mayor with $perk")
	}

	@Command
	fun assumeJerry(mayor: Mayor) {
		MayorAPI.suppressAutoUpdate = true
		MayorAPI.currentMayor = Mayor.JERRY.withAll()
		MayorAPI.currentMinister = Mayor.UNKNOWN.withNone()
		MayorAPI.jerryMayor = mayor.withAll() to Timestamp.distantFuture()
		ChatUtils.addMessage("Assuming Jerry mayor with $mayor perks")
	}
}