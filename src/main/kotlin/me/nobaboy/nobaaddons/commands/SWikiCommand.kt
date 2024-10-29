package me.nobaboy.nobaaddons.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.utils.OSUtils
import me.nobaboy.nobaaddons.utils.StringUtils.title
import me.nobaboy.nobaaddons.utils.TextUtils.buildText
import me.nobaboy.nobaaddons.utils.TextUtils.toText
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.command.CommandRegistryAccess
import net.minecraft.text.ClickEvent
import net.minecraft.text.HoverEvent
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting

object SWikiCommand {
	private val config get() = NobaConfigManager.config.general

	fun init() {
		ClientCommandRegistrationCallback.EVENT.register(this::register)
	}

	private fun register(dispatcher: CommandDispatcher<FabricClientCommandSource>, ignored: CommandRegistryAccess) {
		dispatcher.register(ClientCommandManager.literal("swiki")
			.then(ClientCommandManager.argument("query", StringArgumentType.greedyString())
				.executes(this::searchWiki)))
	}

	private fun searchWiki(ctx: CommandContext<FabricClientCommandSource>): Int {
		val query = StringArgumentType.getString(ctx, "query").title()
		val wikiName = Text.literal("Official SkyBlock Wiki").formatted(Formatting.DARK_AQUA, Formatting.BOLD)
		val link = "https://wiki.hypixel.net/index.php?search=${query.replace(" ", "+")}&scope=internal"

		if(config.wikiCommandAutoOpen) {
			val message = compileAutoOpenMessage(query, wikiName)
			ChatUtils.addMessage(message)
			OSUtils.browse(link)
			return Command.SINGLE_SUCCESS
		}

		val message = compileClickWikiMessage(query, wikiName)
		val hoverText = Text.literal("View '$query' on the Official SkyBlock Wiki").formatted(Formatting.GRAY)
		message.style = Style.EMPTY
			.withClickEvent(ClickEvent(ClickEvent.Action.OPEN_URL, link))
			.withHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText))

		ChatUtils.addMessage(message)
		return Command.SINGLE_SUCCESS
	}

	private fun compileAutoOpenMessage(query: String, wikiName: Text) = buildText {
		formatted(Formatting.AQUA)
		append("Opening the ")
		append(wikiName)
		append(" with search query '$query'.")
	}

	private fun compileClickWikiMessage(query: String, wikiName: Text) = buildText {
		formatted(Formatting.AQUA)
		append("Click ")
		append("HERE".toText().formatted(Formatting.DARK_AQUA, Formatting.BOLD))
		append(" to find '$query' on the ")
		append(wikiName)
		append(".")
	}
}