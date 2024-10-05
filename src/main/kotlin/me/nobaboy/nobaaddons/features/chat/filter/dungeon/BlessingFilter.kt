package me.nobaboy.nobaaddons.features.chat.filter.dungeon

import me.nobaboy.nobaaddons.api.SkyblockAPI.inIsland
import me.nobaboy.nobaaddons.api.data.IslandType
import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.config.impl.ChatFilterOption
import me.nobaboy.nobaaddons.utils.RegexUtils.matchMatcher
import me.nobaboy.nobaaddons.utils.StringUtils.clean
import me.nobaboy.nobaaddons.utils.StringUtils.startsWith
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import java.util.regex.Pattern

object BlessingFilter {
    private val config get() = NobaConfigManager.get().chat.filter

    private val blessingFindPattern: Pattern = Pattern.compile(
        "^DUNGEON BUFF! ([A-z0-9_]+ found a|A) Blessing of (?<blessing>[A-z]+) [IV]+( was found)?!( \\([A-z0-9 ]+\\))?"
    )
    private val blessingStatsPattern: Pattern = Pattern.compile(
        "(?<one>\\+[\\d.]+x?)( & )?(?<two>\\+[\\d.]+x?)? (?<stat>❁ Strength|☠ Crit Damage|❈ Defense|❁ Damage|HP|❣ Health Regen|✦ Speed|✎ Intelligence)"
    )

    private val blessingLines = mutableListOf<Text>()
    private val blessingDetails = mutableListOf<String>()
    private var expectedBlessingLines: Int? = null

    fun init() {
        ClientReceiveMessageEvents.ALLOW_GAME.register { message, _ ->
            return@register processMessage(message.string.clean())
        }
    }

    private fun processMessage(message: String): Boolean {
        if (!isEnabled()) return true

        val option = config.blessingMessage
        if (processOptionType(option)) return true
        if (option == ChatFilterOption.HIDDEN) return false

        blessingFindPattern.matchMatcher(message) {
            val blessingType = BlessingCategory.valueOf(group("blessing").uppercase())
            expectedBlessingLines = if (blessingType.text.contains("POWER")) 2 else 1
            blessingLines.add(Text.literal(blessingType.text).setStyle(blessingType.style))
            return false
        }

        if (message.startsWith(listOf("     Granted you", "     Also granted you"))) {
            blessingDetails.add(message.trim())
            if (blessingDetails.size == expectedBlessingLines) {
                compileAndSendBlessingMessage(option == ChatFilterOption.ACTION_BAR)
            }
             return false
        }

        return true
    }

    private fun compileAndSendBlessingMessage(actionBar: Boolean) {
        val detailsLine = StringBuilder()

        val blessingDetailsMatches = blessingStatsPattern.toRegex().findAll(blessingDetails.joinToString())

        blessingDetailsMatches.forEachIndexed { index, match ->
            val stat = match.groups["stat"]!!.value
            val one = match.groups["one"]!!.value
            val two = match.groups["two"]?.value

            detailsLine.append(" $one")
            if (two != null) detailsLine.append(" & $two")
            detailsLine.append(" $stat")

            when (blessingDetailsMatches.count() - index) {
                1 -> detailsLine.append(".")
                2 -> detailsLine.append(" and")
                else -> detailsLine.append(", ")
            }
        }

        blessingLines.add(Text.literal(detailsLine.toString()).setStyle(Style.EMPTY.withColor(Formatting.GRAY)))

        val formattedMessage = Text.empty()
        blessingLines.forEach { formattedMessage.append(it) }

        ChatUtils.addMessage(formattedMessage, false, actionBar)
        blessingDetails.clear()
        blessingLines.clear()
    }

    private fun processOptionType(option: ChatFilterOption): Boolean {
        return when (option) {
            ChatFilterOption.SHOWN -> true
            ChatFilterOption.COMPACT, ChatFilterOption.ACTION_BAR -> false
            ChatFilterOption.HIDDEN -> false
        }
    }

    private enum class BlessingCategory(val text: String, val style: Style) {
        POWER("POWER BUFF!", Style.EMPTY.withColor(Formatting.RED).withBold(true)),
        WISDOM("WISDOM BUFF!", Style.EMPTY.withColor(Formatting.BLUE).withBold(true)),
        STONE("STONE BUFF!", Style.EMPTY.withColor(Formatting.DARK_GRAY).withBold(true)),
        LIFE("LIFE BUFF!", Style.EMPTY.withColor(Formatting.LIGHT_PURPLE).withBold(true)),
        TIME("TIME BUFF!", Style.EMPTY.withColor(Formatting.DARK_GREEN).withBold(true));
    }

    private fun isEnabled() = IslandType.DUNGEONS.inIsland()
}