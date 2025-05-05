package me.nobaboy.nobaaddons.features.slayers

import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.events.impl.chat.ChatMessageEvents
import me.nobaboy.nobaaddons.repo.Repo.fromRepo
import me.nobaboy.nobaaddons.utils.CommonPatterns
import me.nobaboy.nobaaddons.utils.NumberUtils.addSeparators
import me.nobaboy.nobaaddons.utils.RegexUtils.firstFullMatch
import me.nobaboy.nobaaddons.utils.RegexUtils.onFullMatch
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import me.nobaboy.nobaaddons.utils.StringUtils.toAbbreviatedString
import me.nobaboy.nobaaddons.utils.mc.TextUtils.bold
import me.nobaboy.nobaaddons.utils.mc.TextUtils.buildText
import me.nobaboy.nobaaddons.utils.mc.TextUtils.command
import me.nobaboy.nobaaddons.utils.mc.TextUtils.darkPurple
import me.nobaboy.nobaaddons.utils.mc.TextUtils.gray
import me.nobaboy.nobaaddons.utils.mc.TextUtils.green
import me.nobaboy.nobaaddons.utils.mc.TextUtils.hoverText
import me.nobaboy.nobaaddons.utils.mc.TextUtils.lightPurple
import me.nobaboy.nobaaddons.utils.mc.TextUtils.runCommand
import me.nobaboy.nobaaddons.utils.mc.TextUtils.toText
import me.nobaboy.nobaaddons.utils.mc.TextUtils.yellow
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import me.nobaboy.nobaaddons.utils.chat.Message
import me.nobaboy.nobaaddons.utils.tr

@Suppress("RegExpSimplifiable") // [ ] is used to make it clear the spaces are not a mistake
object CompactSlayerMessages {
	private val config get() = NobaConfig.slayers.compactMessages

	private val QUEST_STARTED_REGEX by Regex("^[ ]+SLAYER QUEST STARTED!").fromRepo("slayer.quest_started")

	private val SLAY_TO_SPAWN_REGEX by Regex("^[ ]+» Slay [\\d,]+ Combat XP worth of .+").fromRepo("slayer.slay_to_spawn")
	private val TALK_TO_MADDOX_REGEX by Regex("^[ ]+» Talk to Maddox to claim your [A-z]+ Slayer XP!").fromRepo("slayer.talk_to_maddox")

	private val SLAYER_LEVEL_REGEX by Regex("^[ ]+(?<slayer>[A-z]+) Slayer LVL (?<level>\\d) - (?:LVL MAXED OUT|Next LVL in (?<nextLevel>[\\d,]+) XP)!").fromRepo("slayer.level")
	private val RNG_METER_REGEX by Regex("^[ ]+RNG Meter - (?<xp>[\\d,]+) Stored XP").fromRepo("slayer.rng_meter")

	private var lastMessage: Message? = null

	// used to determine when the compacted message should be sent
	private var autoSlayer = false

	private var slayer: String? = null
	private var level: Pair<Int, Int?>? = null
	private var rngMeter: Pair<Int, String>? = null

	fun init() {
		ChatMessageEvents.ALLOW.register(this::onChatMessage)
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
		if(config.removeLastMessage) lastMessage?.remove()
		lastMessage = ChatUtils.addAndCaptureMessage(compile(), prefix = false, color = null)
	}

	// Completing a quest with auto slayer:
	//  QUEST_COMPLETE_REGEX
	//  SLAYER_LEVEL_REGEX
	//  RNG_METER_REGEX
	//  QUEST_STARTED_REGEX
	//  SLAY_TO_SPAWN_REGEX

	// Completing a quest without auto slayer:
	//  BOSS_SLAIN_REGEX
	//  TALK_TO_MADDOX_REGEX

	// Claiming at Maddox:
	//  QUEST_COMPLETE_REGEX
	//  SLAYER_LEVEL_REGEX
	//  RNG_METER_REGEX

	// We're canceling messages from completing a slayer quest with auto slayer active, and when claiming at Maddox;
	// quest completion messages without auto slayer are also used to know the correct order, but we don't
	// cancel those messages.
	private fun onChatMessage(event: ChatMessageEvents.Allow) {
		if(!config.enabled) return

		val text = event.message
		val string = text.string.cleanFormatting()

		CommonPatterns.SLAYER_QUEST_COMPLETE_REGEX.onFullMatch(string) {
			slayer = null
			level = null
			rngMeter = null
			event.cancel()
			return
		}

		listOf(CommonPatterns.SLAYER_BOSS_SLAIN_REGEX, TALK_TO_MADDOX_REGEX).firstFullMatch(string) {
			autoSlayer = false
			return
		}

		SLAYER_LEVEL_REGEX.onFullMatch(string) {
			slayer = groups["slayer"]!!.value
			level = groups["level"]!!.value.toInt() to groups["nextLevel"]?.value?.replace(",", "")?.toIntOrNull()
			event.cancel()
			return
		}

		RNG_METER_REGEX.onFullMatch(string) {
			rngMeter = groups["xp"]!!.value.replace(",", "").toInt() to text.style.clickEvent!!.command()
			if(!autoSlayer) send()
			event.cancel()
			return
		}

		QUEST_STARTED_REGEX.onFullMatch(string) {
			event.cancel()
			return
		}

		SLAY_TO_SPAWN_REGEX.onFullMatch(string) {
			if(autoSlayer) send()
			autoSlayer = true
			event.cancel()
			return
		}
	}
}