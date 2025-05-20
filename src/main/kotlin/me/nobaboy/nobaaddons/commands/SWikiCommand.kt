package me.nobaboy.nobaaddons.commands

import com.mojang.authlib.HttpAuthenticationService
import dev.celestialfault.commander.annotations.Command
import dev.celestialfault.commander.annotations.Greedy
import me.nobaboy.nobaaddons.api.HypixelAPI
import me.nobaboy.nobaaddons.commands.impl.CommandUtil
import me.nobaboy.nobaaddons.commands.impl.NobaClientCommand
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.utils.StringUtils.title
import me.nobaboy.nobaaddons.utils.mc.TextUtils.hoverText
import me.nobaboy.nobaaddons.utils.mc.TextUtils.openUrl
import me.nobaboy.nobaaddons.utils.mc.chat.ChatUtils
import me.nobaboy.nobaaddons.utils.tr
import me.owdding.ktmodules.Module
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Util

// TODO change this to replace the response of /wiki if invalid
@Module
object SWikiCommand {
	private val config get() = NobaConfig.general
	private val commander by CommandUtil::commander

	init {
		ClientCommandRegistrationCallback.EVENT.register { dispatch, access ->
			if(HypixelAPI.onHypixel) {
				commander.register(NobaClientCommand(::swiki, this), dispatch)
			}
		}
	}

	@Command(aliases = ["wikisearch"])
	fun swiki(query: @Greedy String) {
		val query = query.title()
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