package me.nobaboy.nobaaddons.api

import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.api.SkyBlockAPI.inIsland
import me.nobaboy.nobaaddons.core.SkyBlockIsland
import me.nobaboy.nobaaddons.core.dungeons.DungeonBoss
import me.nobaboy.nobaaddons.core.dungeons.DungeonClass
import me.nobaboy.nobaaddons.core.dungeons.DungeonFloor
import me.nobaboy.nobaaddons.events.SecondPassedEvent
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.ScoreboardUtils
import me.nobaboy.nobaaddons.utils.ScoreboardUtils.cleanScoreboard
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents

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
	fun inBoss(): Boolean = currentBoss != DungeonBoss.UNKNOWN

	fun init() {
		SecondPassedEvent.EVENT.register { update() }
		ClientReceiveMessageEvents.GAME.register { message, _ -> getBossType(message.string.cleanFormatting()) }
	}

	private fun getClassType() {
		val playerName = MCUtils.playerName!!
		val players = MCUtils.networkHandler!!.playerList
		for(player in players) {
			if(player == null || player.displayName == null) continue
			val text = player.displayName!!.string.cleanFormatting()

			if(text.contains(playerName) && text.indexOf("(") != -1) {
				if(text.contains("($playerName)")) continue // Puzzle fail text
				val dungeonClass = text.substring(text.indexOf("(") + 1, text.lastIndexOf(")"))
				currentClass = DungeonClass.valueOf(dungeonClass.split(" ")[0].uppercase())
			}
		}
	}

	private fun getFloorType() {
		val scoreboard = ScoreboardUtils.getSidebarLines()
		for(line in scoreboard) {
			val cleanedLine = line.cleanScoreboard()
			if(!cleanedLine.contains("The Catacombs (")) continue

			val floor = cleanedLine.substring(cleanedLine.indexOf("(") + 1, cleanedLine.lastIndexOf(")"))

			currentFloor = runCatching {
				DungeonFloor.valueOf(floor)
			}.getOrElse {
				NobaAddons.LOGGER.error("Unexpected floor type value '$floor'", it)
				DungeonFloor.NONE
			}

			break
		}
	}

	private fun update() {
		if(!SkyBlockIsland.DUNGEONS.inIsland()) {
			currentClass = DungeonClass.EMPTY
			currentFloor = DungeonFloor.NONE
			return
		}

		getClassType()
		getFloorType()
	}

	private fun getBossType(message: String) {
		if(!SkyBlockIsland.DUNGEONS.inIsland()) {
			currentBoss = DungeonBoss.UNKNOWN
			return
		}

		if(!message.startsWith("[BOSS]")) return
		currentBoss = DungeonBoss.getByMessage(message)
	}
}