package me.nobaboy.nobaaddons.api.skyblock

import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI.inIsland
import me.nobaboy.nobaaddons.core.SkyBlockIsland
import me.nobaboy.nobaaddons.core.dungeons.DungeonBoss
import me.nobaboy.nobaaddons.core.dungeons.DungeonClass
import me.nobaboy.nobaaddons.core.dungeons.DungeonFloor
import me.nobaboy.nobaaddons.events.impl.chat.ChatMessageEvents
import me.nobaboy.nobaaddons.events.impl.client.TickEvents
import me.nobaboy.nobaaddons.events.impl.skyblock.SkyBlockEvents
import me.nobaboy.nobaaddons.repo.Repo.fromRepo
import me.nobaboy.nobaaddons.utils.mc.MCUtils
import me.nobaboy.nobaaddons.utils.RegexUtils.onFullMatch
import me.nobaboy.nobaaddons.utils.mc.ScoreboardUtils
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting

object DungeonsAPI {
	private val BOSS_DIALOGUE_REGEX by Regex("^\\[BOSS] (?<name>[A-z ]+):.*").fromRepo("dungeons.boss_dialogue")

	var currentClass: DungeonClass = DungeonClass.UNKNOWN
		private set
	var currentFloor: DungeonFloor = DungeonFloor.UNKNOWN
		private set
	var currentBoss: DungeonBoss = DungeonBoss.UNKNOWN
		private set

	fun isClass(classType: DungeonClass): Boolean = currentClass == classType

	fun inFloor(floor: DungeonFloor): Boolean = currentFloor == floor
	fun inFloor(number: Int): Boolean = currentFloor.number == number

	fun isBoss(boss: DungeonBoss): Boolean = currentBoss == boss
	fun inBoss(): Boolean = currentBoss != DungeonBoss.UNKNOWN && currentBoss != DungeonBoss.WATCHER

	fun init() {
		SkyBlockEvents.ISLAND_CHANGE.register { reset() }
		TickEvents.everySecond(this::onSecondPassed)
		ChatMessageEvents.CHAT.register(this::onChatMessage)
	}

	private fun onSecondPassed(event: TickEvents.Tick) {
		if(!SkyBlockIsland.DUNGEONS.inIsland()) return

		updateClass()
		updateFloor()
	}

	private fun onChatMessage(event: ChatMessageEvents.Chat) {
		if(!SkyBlockIsland.DUNGEONS.inIsland()) return

		BOSS_DIALOGUE_REGEX.onFullMatch(event.cleaned) {
			val name = groups["name"]?.value ?: return
			currentBoss = DungeonBoss.getByName(name)
		}
	}

	private fun updateClass() {
		val playerName = MCUtils.playerName ?: return
		val playerList = MCUtils.networkHandler?.playerList ?: return

		val classLine = playerList
			.asSequence()
			.mapNotNull { it.displayName?.string?.cleanFormatting() }
			.firstOrNull {
				it.contains(playerName) &&
				it.contains("(") &&
				!it.contains("($playerName)")
			} ?: return

		val className = classLine
			.substringAfter("(")
			.substringBefore(")")
			.split(" ")
			.firstOrNull() ?: return

		currentClass = DungeonClass.getByName(className)
	}

	private fun updateFloor() {
		val scoreboard = ScoreboardUtils.getScoreboardLines()

		val floorLine = scoreboard.firstOrNull { it.contains("The Catacombs (") } ?: return
		val floorAbbreviation = floorLine.substringAfter("(").substringBefore(")")

		currentFloor = DungeonFloor.getByAbbreviation(floorAbbreviation)
	}

	private fun reset() {
		currentClass = DungeonClass.UNKNOWN
		currentFloor = DungeonFloor.UNKNOWN
		currentBoss = DungeonBoss.UNKNOWN
	}
}