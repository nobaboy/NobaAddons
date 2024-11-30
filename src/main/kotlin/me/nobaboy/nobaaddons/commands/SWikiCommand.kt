package me.nobaboy.nobaaddons.commands

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import me.nobaboy.nobaaddons.commands.internal.Command
import me.nobaboy.nobaaddons.commands.internal.CommandUtil
import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.utils.StringUtils.title
import me.nobaboy.nobaaddons.utils.TextUtils.buildText
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.text.ClickEvent
import net.minecraft.text.HoverEvent
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Util

object SWikiCommand {
	private val config get() = NobaConfigManager.config.general

	private val command = Command.command("swiki") {
		buildCommand {
			it.then(ClientCommandManager.argument("query", StringArgumentType.greedyString())
				.executes(this::execute))
		}

		executes(this@SWikiCommand::searchWiki)
	}

	fun init() {
		CommandUtil.register(command)
	}

	private fun searchWiki(ctx: CommandContext<FabricClientCommandSource>) {
		val query = StringArgumentType.getString(ctx, "query").title()
		val wikiName = Text.literal("Official SkyBlock Wiki").formatted(Formatting.DARK_AQUA, Formatting.BOLD)
		val link = "https://wiki.hypixel.net/index.php?search=${query.replace(" ", "+")}&scope=internal"

		if(config.wikiCommandAutoOpen) {
			val message = compileAutoOpenMessage(query, wikiName)
			ChatUtils.addMessage(message)
			Util.getOperatingSystem().open(link)
			return
		}

		val message = compileClickWikiMessage(query, wikiName)
		val hoverText = Text.literal("View '$query' on the Official SkyBlock Wiki").formatted(Formatting.GRAY)
		message.style = message.style
			.withClickEvent(ClickEvent(ClickEvent.Action.OPEN_URL, link))
			.withHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText))

		ChatUtils.addMessage(message)
	}

	private fun compileAutoOpenMessage(query: String, wikiName: Text) = buildText {
		append("Opening the ")
		append(wikiName)
		append(" with search query '$query'.")
	}

	private fun compileClickWikiMessage(query: String, wikiName: Text) = buildText {
		append("Click ")
		append(Text.literal("HERE").formatted(Formatting.DARK_AQUA, Formatting.BOLD))
		append(" to find '$query' on the ")
		append(wikiName)
		append(".")
	}
}