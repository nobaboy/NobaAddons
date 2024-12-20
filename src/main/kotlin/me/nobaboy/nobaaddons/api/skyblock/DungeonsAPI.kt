package me.nobaboy.nobaaddons.api.skyblock

import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI.inIsland
import me.nobaboy.nobaaddons.core.SkyBlockIsland
import me.nobaboy.nobaaddons.core.dungeons.DungeonBoss
import me.nobaboy.nobaaddons.core.dungeons.DungeonClass
import me.nobaboy.nobaaddons.core.dungeons.DungeonFloor
import me.nobaboy.nobaaddons.events.SecondPassedEvent
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.ScoreboardUtils
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
	fun inBoss(): Boolean = currentBoss != DungeonBoss.UNKNOWN && currentBoss != DungeonBoss.WATCHER

	fun init() {
		SecondPassedEvent.EVENT.register { onSecondPassed() }
		ClientReceiveMessageEvents.GAME.register { message, _ -> getBossType(message.string.cleanFormatting()) }
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

		val dungeonClass = playerList.mapNotNull { it?.displayName?.string?.cleanFormatting() }
			.firstOrNull { it.contains(playerName) && it.contains("(") && !it.contains("($playerName)") }
			?.substringAfter("(")
			?.substringBefore(")")
			?.split(" ")
			?.firstOrNull()
			?.uppercase()

		currentClass = dungeonClass?.let { clazz ->
			runCatching { DungeonClass.valueOf(clazz) }
				.getOrElse {
					NobaAddons.LOGGER.error("Unexpected class type value '$clazz'", it)
					DungeonClass.EMPTY
				}
		} ?: DungeonClass.EMPTY
	}

	private fun getFloorType() {
		val lines = ScoreboardUtils.getScoreboardLines()

		val dungeonFloor = lines.firstOrNull { it.contains("The Catacombs (") }
			?.substringAfter("(")?.substringBefore(")")

		currentFloor = dungeonFloor?.let { floor ->
			runCatching { DungeonFloor.valueOf(floor) }
				.getOrElse {
					NobaAddons.LOGGER.error("Unexpected floor type value '$floor'", it)
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