package me.nobaboy.nobaaddons.features.slayers

import me.nobaboy.nobaaddons.config.option.booleanController
import me.nobaboy.nobaaddons.events.impl.chat.ChatMessageEvents
import me.nobaboy.nobaaddons.features.Feature
import me.nobaboy.nobaaddons.features.FeatureCategory
import me.nobaboy.nobaaddons.repo.Repo.fromRepo
import me.nobaboy.nobaaddons.utils.CommonText
import me.nobaboy.nobaaddons.utils.NumberUtils.addSeparators
import me.nobaboy.nobaaddons.utils.RegexUtils.firstFullMatch
import me.nobaboy.nobaaddons.utils.RegexUtils.onFullMatch
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import me.nobaboy.nobaaddons.utils.StringUtils.toAbbreviatedString
import me.nobaboy.nobaaddons.utils.TextUtils.bold
import me.nobaboy.nobaaddons.utils.TextUtils.buildText
import me.nobaboy.nobaaddons.utils.TextUtils.darkPurple
import me.nobaboy.nobaaddons.utils.TextUtils.gray
import me.nobaboy.nobaaddons.utils.TextUtils.green
import me.nobaboy.nobaaddons.utils.TextUtils.hoverText
import me.nobaboy.nobaaddons.utils.TextUtils.lightPurple
import me.nobaboy.nobaaddons.utils.TextUtils.runCommand
import me.nobaboy.nobaaddons.utils.TextUtils.toText
import me.nobaboy.nobaaddons.utils.TextUtils.yellow
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import me.nobaboy.nobaaddons.utils.chat.Message
import me.nobaboy.nobaaddons.utils.tr

@Suppress("RegExpSimplifiable") // [ ] is used to make it clear the spaces are not a mistake
object CompactSlayerMessages : Feature("compactSlayerMessages", tr("nobaaddons.feature.compactSlayerMessages", "Compact Slayer Quest Messages"), FeatureCategory.SLAYER) {
	private var enabled by config(false) {
		name = CommonText.Config.ENABLED
		description = tr("nobaaddons.config.slayers.compactMessages.enabled.tooltip", "Condenses messages from Auto-Slayer and manually claiming a Slayer quest at Maddox into one message while enabled")
		booleanController()
	}

	private var removeLastMessage by config(false) {
		name = tr("nobaaddons.config.slayers.compactMessages.removeLastMessage", "Remove Previous Message")
		description = tr("nobaaddons.config.slayers.compactMessages.removeLastMessage.tooltip", "The last compacted message will also be removed upon completing another slayer quest")
		booleanController()
		requires { option(::enabled) }
	}

	private val SLAYER_QUEST_COMPLETE by Regex("^[ ]+SLAYER QUEST COMPLETE!").fromRepo("slayer.questComplete")
	private val SLAYER_LEVEL by Regex("^[ ]+(?<slayer>[A-z]+) Slayer LVL (?<level>\\d) - (?:LVL MAXED OUT!|Next LVL in (?<nextLevel>[\\d,]+) XP)").fromRepo("slayer.slayerXp")
	private val RNG_METER by Regex("^[ ]+RNG Meter - (?<xp>[\\d,]+) Stored XP").fromRepo("slayer.rngMeter")

	private val SLAYER_QUEST_STARTED by Regex("^[ ]+SLAYER QUEST STARTED!").fromRepo("slayer.questStarted")
	private val SLAYER_QUEST_TO_SPAWN by Regex("^[ ]+» Slay [\\d,]+ Combat XP worth of .+").fromRepo("slayer.toSpawnBoss")

	private val BOSS_SLAIN by Regex("^[ ]+NICE! SLAYER BOSS SLAIN!").fromRepo("slayer.bossSlain")
	private val TALK_TO_MADDOX by Regex("[ ]+» Talk to Maddox to claim your [A-z]+ Slayer XP!").fromRepo("slayer.talkToMaddox")

	private var lastMessage: Message? = null

	// used to determine when the compacted message should be sent
	private var autoslayer = false

	private var slayer: String? = null
	private var level: Pair<Int, Int?>? = null
	private var rngMeter: Pair<Int, String>? = null

	override fun init() {
		listen(ChatMessageEvents.ALLOW, listener = this::onChatMessage)
	}

	private fun compile() = buildText {
		append(tr("nobaaddons.slayer.compact.prefix", "SLAYER COMPLETE!").green().bold())
		append(" ")

		val lvl = level ?: (-1 to null)
		append(tr("nobaaddons.slayer.compact.level", "LVL ${lvl.first}").yellow())
		append(" - ".toText().darkPurple())
		if(lvl.second == null) {
			// TODO it'd be nice to include total xp here, but I don't feel like building out the functionality
			//      required to track that right now
			append(tr("nobaaddons.slayer.compact.maxLevel", "MAX").green().bold())
		} else {
			val toNextLevel = lvl.second?.addSeparators().toText().yellow()
			append(tr("nobaaddons.slayer.compact.toNext", "$toNextLevel XP to next LVL").gray())
		}

		val rngMeter = rngMeter
		if(rngMeter != null) {
			append(" ")
			val xp = rngMeter.first
			append(buildText {
				append(tr("nobaaddons.slayer.compact.storedRngXp", "(${xp.toAbbreviatedString()} stored XP)"))
				runCommand(rngMeter.second)
				hoverText {
					append("${xp.addSeparators()} XP".toText().lightPurple())
					append("\n\n")
					append(tr("nobaaddons.slayer.compact.clickRngMeter", "Click to select RNG Meter drop").yellow())
				}
				lightPurple()
			})
		}

		// TODO include messages from other features (i.e. kill times)
	}

	private fun send() {
		if(removeLastMessage) {
			lastMessage?.remove()
		}
		lastMessage = ChatUtils.addMessage(compile(), prefix = false, color = null)
	}

	// Completing a quest with auto slayer:
	//  SLAYER_QUEST_COMPLETE
	//  SLAYER_LEVEL
	//  RNG_METER
	//  SLAYER_QUEST_STARTED
	//  SLAYER_QUEST_TO_SPAWN

	// Completing a quest without auto slayer:
	//  BOSS_SLAIN
	//  TALK_TO_MADDOX

	// Claiming at Maddox:
	//  SLAYER_QUEST_COMPLETE
	//  SLAYER_LEVEL
	//  RNG_METER

	// We're canceling messages from completing a slayer quest with auto slayer active, and when claiming at Maddox;
	// quest completion messages without auto slayer are also used to know the correct order, but we don't
	// cancel those messages.
	private fun onChatMessage(event: ChatMessageEvents.Allow) {
		if(!enabled) return

		val text = event.message
		val string = text.string.cleanFormatting()

		SLAYER_QUEST_COMPLETE.onFullMatch(string) {
			slayer = null
			level = null
			rngMeter = null
			event.cancel()
			return
		}

		listOf(BOSS_SLAIN, TALK_TO_MADDOX).firstFullMatch(string) {
			autoslayer = false
			return
		}

		SLAYER_LEVEL.onFullMatch(string) {
			slayer = groups["slayer"]!!.value
			level = groups["level"]!!.value.toInt() to groups["nextLevel"]?.value?.replace(",", "")?.toIntOrNull()
			event.cancel()
			return
		}

		RNG_METER.onFullMatch(string) {
			rngMeter = groups["xp"]!!.value.replace(",", "").toInt() to text.style.clickEvent!!.value
			if(!autoslayer) send()
			event.cancel()
			return
		}

		SLAYER_QUEST_STARTED.onFullMatch(string) {
			event.cancel()
			return
		}

		SLAYER_QUEST_TO_SPAWN.onFullMatch(string) {
			if(autoslayer) send()
			autoslayer = true
			event.cancel()
			return
		}
	}
}