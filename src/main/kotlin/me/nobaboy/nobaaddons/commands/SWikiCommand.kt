package me.nobaboy.nobaaddons.commands

import com.mojang.authlib.HttpAuthenticationService
import me.nobaboy.nobaaddons.commands.impl.AnnotatedCommand
import me.nobaboy.nobaaddons.commands.annotations.Command
import me.nobaboy.nobaaddons.commands.impl.CommandUtil
import me.nobaboy.nobaaddons.commands.impl.Context
import me.nobaboy.nobaaddons.commands.annotations.GreedyString
import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.utils.TextUtils.hoverText
import me.nobaboy.nobaaddons.utils.TextUtils.openUrl
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import me.nobaboy.nobaaddons.utils.tr
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Util

// TODO change this to replace the response of /wiki if invalid
object SWikiCommand {
	private val config get() = NobaConfigManager.config.general

	fun init() {
		CommandUtil.register(AnnotatedCommand(this::swiki, this))
	}

	@Command(aliases = ["wikisearch"])
	fun swiki(@Suppress("unused") ctx: Context, query: @GreedyString String) {
		val wikiName = tr("nobaaddons.officialWiki", "Official SkyBlock Wiki").formatted(Formatting.DARK_AQUA, Formatting.BOLD)
		val queryString = HttpAuthenticationService.buildQuery(mapOf("search" to query, "scope" to "internal"))
		val link = "https://wiki.hypixel.net/index.php?$queryString"

		if(config.wikiCommandAutoOpen) {
			val message = compileAutoOpenMessage(query, wikiName)
			ChatUtils.addMessage(message)
			Util.getOperatingSystem().open(link)
			return
		}

		val hoverText = tr("nobaaddons.command.swiki.hover", "View '$query' on the SkyBlock Wiki").formatted(Formatting.GRAY)
		val message = compileClickWikiMessage(query, wikiName).openUrl(link).hoverText(hoverText)

		ChatUtils.addMessage(message)
	}

	private fun compileAutoOpenMessage(query: String, wikiName: Text) =
		tr("nobaaddons.command.swiki.autoOpening", "Opening the $wikiName with search query '$query'")

	private fun compileClickWikiMessage(query: String, wikiName: Text) =
		tr("nobaaddons.command.swiki.response", "Click to find '$query' on the $wikiName")
}