package me.nobaboy.nobaaddons.api

import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.api.SkyBlockAPI.inIsland
import me.nobaboy.nobaaddons.api.data.BossType
import me.nobaboy.nobaaddons.api.data.ClassType
import me.nobaboy.nobaaddons.api.data.FloorType
import me.nobaboy.nobaaddons.api.data.IslandType
import me.nobaboy.nobaaddons.events.SecondPassedEvent
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.ScoreboardUtils
import me.nobaboy.nobaaddons.utils.ScoreboardUtils.cleanScoreboard
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents

object DungeonsAPI {
	private var currentClass: ClassType = ClassType.EMPTY
	private var currentFloor: FloorType = FloorType.NONE
	private var currentBoss: BossType = BossType.UNKNOWN

	fun getClass(): ClassType = currentClass
	fun isClass(classType: ClassType): Boolean = currentClass == classType

	fun getFloor(): FloorType = currentFloor
	fun inFloor(floor: Int): Boolean = currentFloor.floor == floor
	fun inFloor(floor: FloorType): Boolean = currentFloor == floor

	fun getBoss(): BossType = currentBoss
	fun isBoss(boss: BossType): Boolean = currentBoss == boss
	fun inBoss(): Boolean = currentBoss != BossType.UNKNOWN

	fun init() {
		SecondPassedEvent.EVENT.register { getClassType() }
		SecondPassedEvent.EVENT.register { getFloorType() }
		ClientReceiveMessageEvents.GAME.register { message, _ ->
			getBossType(message.string.cleanFormatting())
		}
	}

	private fun getClassType() {
		if(!IslandType.DUNGEONS.inIsland()) {
			currentClass = ClassType.EMPTY
			return
		}

		val playerName = MCUtils.playerName!!
		val players = MCUtils.networkHandler!!.playerList
		for(player in players) {
			if(player == null || player.displayName == null) continue
			val text = player.displayName!!.string.cleanFormatting()

			if(text.contains(playerName) && text.indexOf("(") != -1) {
				if(text.contains("($playerName)")) continue // Puzzle fail text
				val dungeonClass = text.substring(text.indexOf("(") + 1, text.lastIndexOf(")"))
				currentClass = ClassType.valueOf(dungeonClass.split(" ")[0].uppercase())
			}
		}
	}

	private fun getFloorType() {
		if(!IslandType.DUNGEONS.inIsland()) {
			currentFloor = FloorType.NONE
			return
		}

		val scoreboard = ScoreboardUtils.getSidebarLines()
		for(line in scoreboard) {
			val cleanedLine = line.cleanScoreboard()
			if(!cleanedLine.contains("The Catacombs (")) continue

			val floor = cleanedLine.substring(cleanedLine.indexOf("(") + 1, cleanedLine.lastIndexOf(")"))

			currentFloor = runCatching {
				FloorType.valueOf(floor)
			}.getOrElse {
				NobaAddons.LOGGER.error("Unexpected floor type value '$floor'", it)
				FloorType.NONE
			}

			break
		}
	}

	private fun getBossType(message: String) {
		if(!IslandType.DUNGEONS.inIsland()) {
			currentBoss = BossType.UNKNOWN
			return
		}

		if(!message.startsWith("[BOSS]")) return
		currentBoss = BossType.fromChat(message)
	}
}