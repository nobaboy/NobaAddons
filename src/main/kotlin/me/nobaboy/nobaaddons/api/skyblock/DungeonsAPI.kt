package me.nobaboy.nobaaddons.api.skyblock

import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI.inIsland
import me.nobaboy.nobaaddons.core.SkyBlockIsland
import me.nobaboy.nobaaddons.core.dungeons.DungeonBoss
import me.nobaboy.nobaaddons.core.dungeons.DungeonClass
import me.nobaboy.nobaaddons.core.dungeons.DungeonFloor
import me.nobaboy.nobaaddons.events.impl.chat.ChatMessageEvents
import me.nobaboy.nobaaddons.events.impl.client.TickEvent
import me.nobaboy.nobaaddons.utils.ErrorManager
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.ScoreboardUtils
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting

object DungeonsAPI {
	var currentClass: DungeonClass = DungeonClass.EMPTY
		private set
	var currentFloor: DungeonFloor = DungeonFloor.NONE
		private set
	var currentBoss: DungeonBoss = DungeonBoss.UNKNOWN
		private set

	fun isClass(classType: DungeonClass): Boolean = currentClass == classType

	fun inFloor(floor: DungeonFloor): Boolean = currentFloor == floor
	fun inFloor(floor: Int): Boolean = currentFloor.floor == floor

	fun isBoss(boss: DungeonBoss): Boolean = currentBoss == boss
	fun inBoss(): Boolean = currentBoss != DungeonBoss.UNKNOWN && currentBoss != DungeonBoss.WATCHER

	fun init() {
		TickEvent.everySecond { onSecondPassed() }
		ChatMessageEvents.CHAT.register { (message) -> getBossType(message.string.cleanFormatting()) }
	}

	private fun onSecondPassed() {
		if(!SkyBlockIsland.DUNGEONS.inIsland()) {
			currentClass = DungeonClass.EMPTY
			currentFloor = DungeonFloor.NONE
			return
		}

		getClassType()
		getFloorType()
	}

	private fun getClassType() {
		val playerName = MCUtils.playerName ?: return
		val playerList = MCUtils.networkHandler?.playerList ?: return

		val fullLine = playerList.mapNotNull { it?.displayName?.string?.cleanFormatting() }
			.firstOrNull { it.contains(playerName) && it.contains("(") && !it.contains("($playerName)") }
		val dungeonClass = fullLine
			?.substringAfter("(")
			?.substringBefore(")")
			?.split(" ")
			?.firstOrNull()
			?.uppercase()

		currentClass = dungeonClass?.let { clazz ->
			runCatching { DungeonClass.valueOf(clazz) }
				.getOrElse {
					ErrorManager.logError("Found unknown Dungeon class", it, "Detected class" to clazz, "In line" to fullLine)
					DungeonClass.EMPTY
				}
		} ?: DungeonClass.EMPTY
	}

	private fun getFloorType() {
		val scoreboard = ScoreboardUtils.getScoreboardLines()

		val fullLine = scoreboard.firstOrNull { it.contains("The Catacombs (") }
		val dungeonFloor = fullLine?.substringAfter("(")?.substringBefore(")")

		currentFloor = dungeonFloor?.let { floor ->
			runCatching { DungeonFloor.valueOf(floor) }
				.getOrElse {
					ErrorManager.logError("Found unknown Dungeon floor", it, "Detected floor" to floor, "In line" to fullLine)
					DungeonFloor.NONE
				}
		} ?: DungeonFloor.NONE
	}

	private fun getBossType(message: String) {
		if(!SkyBlockIsland.DUNGEONS.inIsland()) {
			currentBoss = DungeonBoss.UNKNOWN
			return
		}

		if(message.startsWith("[BOSS]")) currentBoss = DungeonBoss.getByMessage(message)
	}
}