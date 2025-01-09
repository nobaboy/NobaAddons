package me.nobaboy.nobaaddons.api.skyblock

import me.nobaboy.nobaaddons.core.slayer.SlayerBoss
import me.nobaboy.nobaaddons.events.skyblock.SlayerEvents
import me.nobaboy.nobaaddons.repo.Repo.fromRepo
import me.nobaboy.nobaaddons.utils.CollectionUtils.nextAfter
import me.nobaboy.nobaaddons.utils.EntityUtils
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.RegexUtils.onFullMatch
import me.nobaboy.nobaaddons.utils.ScoreboardUtils
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.decoration.ArmorStandEntity

object SlayerAPI {
	private val slayerNamePattern by Regex("^☠ (?<name>[A-z ]+?)(?: (?<tier>[IV]+))? (?<hp>[\\d/BMk.,❤]+)\$").fromRepo("slayer.name")
	private val slayerProgressPattern by Regex("^(?<current>\\d+)/(?<required>\\d+) Kills").fromRepo("slayer.progress")

	var currentQuest: SlayerQuest? = null
		private set

	private val miniBosses = mutableSetOf<LivingEntity>()

	fun init() {
		ClientTickEvents.END_CLIENT_TICK.register { onTick() }
		ClientReceiveMessageEvents.GAME.register { message, _ -> onChatMessage(message.string.cleanFormatting()) }
	}

	private fun onTick() {
		if(!SkyBlockAPI.inSkyBlock) return

		val scoreboard = ScoreboardUtils.getScoreboardLines()
		val bossNameLine = scoreboard.nextAfter("Slayer Quest") ?: return
		val slayerBoss = SlayerBoss.getByName(bossNameLine) ?: return

		if(currentQuest?.boss != slayerBoss) currentQuest = SlayerQuest(slayerBoss)

		val progressLine = scoreboard.nextAfter("Slayer Quest", 2).orEmpty()
		slayerProgressPattern.onFullMatch(progressLine) {
			val current = groups["current"]?.value?.toInt() ?: return@onFullMatch
			val required = groups["required"]?.value?.toInt() ?: return@onFullMatch

			currentQuest?.progress = (current / required) * 100
		}

		val previousState = currentQuest?.spawned
		currentQuest?.spawned = scoreboard.any { it == "Slay the boss!" }
		if(previousState == false && currentQuest?.spawned == true) SlayerEvents.BOSS_SPAWN.invoke(SlayerEvents.BossSpawn())

		currentQuest?.let {
			EntityUtils.getEntities(it.boss.entity)
				.filterIsInstance<LivingEntity>()
				.filterNot { it in miniBosses }
				.forEach { entity ->
					val armorStand = EntityUtils.getNextEntity(entity, 1) as? ArmorStandEntity ?: return@forEach
					val armorStandName = armorStand.name.string

					slayerNamePattern.onFullMatch(armorStandName) {
						if(it.entity != null) return@forEach

						val ownerArmorStand = EntityUtils.getNextEntity(armorStand, 2) as? ArmorStandEntity ?: return@forEach
						val playerName = MCUtils.playerName ?: return@forEach

						if(ownerArmorStand.name.string != "Spawned by: $playerName") return@forEach
						val timerArmorStand = EntityUtils.getNextEntity(armorStand, 1) as? ArmorStandEntity ?: return@forEach

						it.entity = entity
						it.armorStand = armorStand
						it.timerArmorStand = timerArmorStand

						SlayerEvents.FIND_BOSS.invoke(SlayerEvents.Find(entity))
						return@forEach
					}

					if(it.boss.miniBossType?.names?.any { armorStandName.contains(it) } == true) {
						SlayerEvents.FIND_MINI_BOSS.invoke(SlayerEvents.Find(entity))
						miniBosses.add(entity)
					}
				}
		}
	}

	private fun onChatMessage(message: String) {
		if(!SkyBlockAPI.inSkyBlock) return
		if(currentQuest == null) return

		when(message.trim()) {
			"SLAYER QUEST FAILED!", "Your Slayer Quest has been cancelled!" -> currentQuest = null
			"SLAYER QUEST COMPLETE!", "NICE! SLAYER BOSS SLAIN!" -> {
				SlayerEvents.BOSS_KILL.invoke(SlayerEvents.BossKill(currentQuest?.entity, currentQuest?.timerArmorStand))
				currentQuest?.spawned = false
				currentQuest?.entity = null
			}
		}
	}

	data class SlayerQuest(
		val boss: SlayerBoss,
		var progress: Int = 0,
		var spawned: Boolean = false,
		var entity: LivingEntity? = null,
		var armorStand: ArmorStandEntity? = null,
		var timerArmorStand: ArmorStandEntity? = null
	)
}