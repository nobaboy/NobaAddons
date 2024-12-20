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
import me.nobaboy.nobaaddons.utils.Timestamp
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

		val lines = ScoreboardUtils.getSidebarLines()
		val questLine = lines.nextAfter("Slayer Quest") ?: return
		val boss = SlayerBoss.getByName(questLine) ?: return

		if(currentQuest?.boss != boss) currentQuest = SlayerQuest(boss)

		val previousState = currentQuest?.spawned
		currentQuest?.spawned = lines.any { it == "Slay the boss!" }

		// TODO: Remove this, pass in the boss entity with kill event and calculate time to kill using entity age
		if(previousState == false && currentQuest?.spawned == true) {
			SlayerEvents.BOSS_SPAWN.invoke(SlayerEvents.BossSpawn(Timestamp.now()))
		}

		currentQuest?.boss?.entity?.let { entityClass ->
			EntityUtils.getEntities(entityClass).forEach { entity ->
				val armorStand = EntityUtils.getNextEntity(entity, 1) as? ArmorStandEntity ?: return@forEach
				slayerNamePattern.matchMatcher(armorStand.name.string) {
					val ownerArmorStand = EntityUtils.getNextEntity(armorStand, 1) as? ArmorStandEntity ?: return
					val playerName = MCUtils.playerName ?: return

					if(ownerArmorStand.name.string == "Spawned by: $playerName") currentQuest?.entity = entity
				}

				if(entity.age > 1) return@forEach

				SlayerMiniBoss.getByName(armorStand.name.string)?.let {
					SlayerEvents.MINI_BOSS_SPAWN.invoke(SlayerEvents.MiniBossSpawn(entity))
					miniBosses.add(entity)
				}
			}
		}
	}

	private fun onChatMessage(message: String) {
		if(!SkyBlockAPI.inSkyBlock) return

		when(message.trim()) {
			"SLAYER QUEST FAILED!", "Your Slayer Quest has been cancelled!" -> currentQuest?.spawned = false
			"SLAYER QUEST COMPLETE!" -> {
				SlayerEvents.BOSS_KILL.invoke(SlayerEvents.BossKill())
				currentQuest?.spawned = false
			}
		}
	}

	data class SlayerQuest(val boss: SlayerBoss?, var entity: Entity? = null, var spawned: Boolean = false)
}