package me.nobaboy.nobaaddons.api.skyblock

import me.nobaboy.nobaaddons.core.slayer.SlayerBoss
import me.nobaboy.nobaaddons.core.slayer.SlayerMiniBoss
import me.nobaboy.nobaaddons.events.skyblock.SlayerEvents
import me.nobaboy.nobaaddons.utils.CollectionUtils.nextAfter
import me.nobaboy.nobaaddons.utils.EntityUtils
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.RegexUtils.matchMatcher
import me.nobaboy.nobaaddons.utils.ScoreboardUtils
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.entity.Entity
import net.minecraft.entity.decoration.ArmorStandEntity
import java.util.regex.Pattern

object SlayerAPI {
	private val slayerNamePattern = Pattern.compile("^☠ (?<name>[A-z ]+?)(?: (?<tier>[IV]+))? (?<hp>[\\d/BMk.,❤]+)\$")

	var currentQuest: SlayerQuest? = null
		private set

	val miniBosses = mutableSetOf<Entity>()

	fun init() {
		ClientTickEvents.END_CLIENT_TICK.register { onTick() }
		ClientReceiveMessageEvents.GAME.register { message, _ -> onChatMessage(message.string.cleanFormatting()) }
	}

	private fun onTick() {
		if(!SkyBlockAPI.inSkyBlock) return

		val lines = ScoreboardUtils.getScoreboardLines()
		val questLine = lines.nextAfter("Slayer Quest") ?: return
		val boss = SlayerBoss.getByName(questLine) ?: return

		if(currentQuest?.boss != boss) currentQuest = SlayerQuest(boss)
		currentQuest?.spawned = lines.any { it == "Slay the boss!" }

		currentQuest?.boss?.entity?.let { entityClass ->
			EntityUtils.getEntities(entityClass).forEach { entity ->
				if(entity in miniBosses) return@forEach

				val armorStand = EntityUtils.getNextEntity(entity, 1) as? ArmorStandEntity ?: return@forEach

				slayerNamePattern.matchMatcher(armorStand.name.string) {
					val ownerArmorStand = EntityUtils.getNextEntity(armorStand, 2) as? ArmorStandEntity ?: return@forEach
					val playerName = MCUtils.playerName ?: return@forEach

					if(ownerArmorStand.name.string != "Spawned by: $playerName") return
					val timerEntity = EntityUtils.getNextEntity(armorStand, 1) as? ArmorStandEntity ?: return@forEach

					currentQuest?.let {
						it.entity = entity
						it.timerEntity = timerEntity
					}

					return@forEach
				}

				SlayerMiniBoss.getByName(armorStand.name.string)?.let {
					if(entity.age <= 20) SlayerEvents.MINI_BOSS_SPAWN.invoke(SlayerEvents.MiniBossSpawn(entity))
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
			"SLAYER QUEST COMPLETE!" -> {
				SlayerEvents.BOSS_KILL.invoke(SlayerEvents.BossKill(currentQuest?.entity, currentQuest?.timerEntity))
				currentQuest?.spawned = false
			}
		}
	}

	data class SlayerQuest(
		val boss: SlayerBoss?,
		var spawned: Boolean = false,
		var entity: Entity? = null,
		var timerEntity: Entity? = null
	)
}