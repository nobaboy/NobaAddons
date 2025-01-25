package me.nobaboy.nobaaddons.features.slayers

import me.nobaboy.nobaaddons.events.impl.chat.ChatMessageEvents
import me.nobaboy.nobaaddons.repo.Repo.fromRepo
import me.nobaboy.nobaaddons.utils.Timestamp
import me.nobaboy.nobaaddons.utils.chat.Message

@Suppress("RegExpSimplifiable") // [ ] is used to make it clear the spaces are not a mistake
object CompactSlayerMessages {
	private val SLAYER_QUEST_COMPLETE by Regex("^[ ]+SLAYER QUEST COMPLETE!").fromRepo("slayer.questComplete")
	private val SLAYER_LEVEL by Regex("^[ ]+(?<slayer>[A-z]+) LVL (?<level>\\d) - (?:LVL MAXED OUT!|Next LVL in (?<nextLevel>[\\d,]+) XP)").fromRepo("slayer.slayerXp")
	private val RNG_METER by Regex("^[ ]+RNG Meter - [\\d,]+ Stored XP").fromRepo("slayer.rngMeter")

	private val SLAYER_QUEST_STARTED by Regex("^[ ]+SLAYER QUEST STARTED!").fromRepo("slayer.questStarted")
	private val SLAYER_QUEST_TO_SPAWN by Regex("^[ ]+» Slay [\\d,]+ Combat XP worth of .+").fromRepo("slayer.toSpawnBoss")

	// With auto slayer:
	// SLAYER_QUEST_COMPLETE
	// SLAYER_LEVEL
	// RNG_METER
	// SLAYER_QUEST_STARTED
	// SLAYER_QUEST_TO_SPAWN

	// Claiming at Maddox:
	// SLAYER_QUEST_COMPLETE
	// SLAYER_LEVEL
	// RNG_METER

	private var lastQuestComplete = Timestamp.distantPast()
	private var lastMessage: Message? = null

	private var level: Pair<Int, Int>? = null // TODO
	// clickEvent=ClickEvent{action=RUN_COMMAND, value='/cb 0942fc74-ca9f-49c1-950b-a64f9074e3b9'}
	private var rngMeter: Pair<Int, String>? = null

	// how i'd like to format this is effectively:
	/*
	buildText {
		append("SLAYER COMPLETE!".green().bold())
		append(" ")
		append(if(max) "MAX LVL".gray() else "${percent}%".hoverText("${current}/${nextLevel}").gray())
		if(rngMeter) {
			append(" (".darkGray())
			val (xp, cb) = rngMeter
			append("${xp} stored XP".lightPurple().clickCommand("/cb ${cb}"))
			append(")".darkGray())
		}
	}
	*/

	fun init() {
		ChatMessageEvents.ALLOW.register(this::onChatMessage)
	}

	private fun onChatMessage(event: ChatMessageEvents.Allow) {
		//
	}
}