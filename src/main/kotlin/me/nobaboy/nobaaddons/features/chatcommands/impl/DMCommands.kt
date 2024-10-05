package me.nobaboy.nobaaddons.features.chatcommands.impl

import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.features.chatcommands.ChatCommandManager
import me.nobaboy.nobaaddons.features.chatcommands.impl.dm.PartyMeCommand
import me.nobaboy.nobaaddons.features.chatcommands.impl.dm.WarpUserCommand
import me.nobaboy.nobaaddons.features.chatcommands.impl.shared.HelpCommand
import me.nobaboy.nobaaddons.features.chatcommands.impl.shared.WarpOutCommand
import me.nobaboy.nobaaddons.features.chatcommands.impl.shared.WarpPlayerHandler
import me.nobaboy.nobaaddons.utils.RegexUtils.matchMatcher
import me.nobaboy.nobaaddons.utils.StringUtils.clean
import me.nobaboy.nobaaddons.utils.StringUtils.lowercaseContains
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import java.util.regex.Matcher
import java.util.regex.Pattern

object DMCommands : ChatCommandManager() {
    private val config get() = NobaConfigManager.get().chatCommands.dm
    private val chatPattern: Pattern =
        Pattern.compile("^From (?:\\[[A-Z+]+] )?(?<username>[A-z0-9_]+): [!?.](?<command>[A-z0-9_]+) ?(?<argument>[A-z0-9_]+)?")

    init {
        register(HelpCommand(this, "msg", config::help))
        register(WarpOutCommand("msg", config::warpOut))
        register(WarpUserCommand())
        register(PartyMeCommand())
    }

    override fun matchMessage(message: String): Matcher? {
        chatPattern.matchMatcher(message) {
            return this
        }
        return null
    }

    fun init() {
        ClientReceiveMessageEvents.GAME.register { message, _ ->
            val cleanMessage = message.string.clean()

            if (WarpPlayerHandler.isWarping) {
                val playerName = WarpPlayerHandler.player
                if (cleanMessage.lowercaseContains("$playerName is already in the party")) {
                    WarpPlayerHandler.reset(true)
                    return@register
                } else if (cleanMessage.lowercaseContains("$playerName joined the party")) {
                    WarpPlayerHandler.playerJoined = true
                    return@register
                }
            }

            processMessage(cleanMessage, isEnabled())
        }
    }

    private fun isEnabled() = config.enabled
}