package me.nobaboy.nobaaddons.api.skyblock

import me.nobaboy.nobaaddons.core.slayer.SlayerBoss
import me.nobaboy.nobaaddons.events.impl.chat.ChatMessageEvents
import me.nobaboy.nobaaddons.events.impl.client.EntityEvents
import me.nobaboy.nobaaddons.events.impl.client.PacketEvents
import me.nobaboy.nobaaddons.events.impl.client.TickEvents
import me.nobaboy.nobaaddons.events.impl.skyblock.SkyBlockEvents
import me.nobaboy.nobaaddons.events.impl.skyblock.SlayerEvents
import me.nobaboy.nobaaddons.utils.CollectionUtils.nextAfter
import me.nobaboy.nobaaddons.utils.EntityUtils
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.ScoreboardUtils
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.decoration.ArmorStandEntity
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket
import net.minecraft.text.Text
import java.util.Optional
import kotlin.jvm.optionals.getOrNull

object SlayerAPI {
	var currentQuest: SlayerQuest? = null
		private set

	private val miniBosses = mutableSetOf<LivingEntity>()

	fun init() {
		SkyBlockEvents.ISLAND_CHANGE.register { reset() }
		TickEvents.TICK.register { onTick() }
		PacketEvents.POST_RECEIVE.register(this::onPacketReceive)
		EntityEvents.POST_RENDER.register(this::onEntityRender)
		ChatMessageEvents.CHAT.register { (message) -> onChatMessage(message.string.cleanFormatting()) }
	}

	private fun onTick() {
		if(!SkyBlockAPI.inSkyBlock) return

		miniBosses.removeIf { !it.isAlive }

		val scoreboard = ScoreboardUtils.getScoreboardLines()
		val bossNameLine = scoreboard.nextAfter("Slayer Quest") ?: return
		val slayerBoss = SlayerBoss.getByName(bossNameLine) ?: return

		if(currentQuest?.boss != slayerBoss) currentQuest = SlayerQuest(slayerBoss)

		val previousState = currentQuest?.spawned
		currentQuest?.spawned = scoreboard.any { it == "Slay the boss!" }
		if(previousState == false && currentQuest?.spawned == true) SlayerEvents.BOSS_SPAWN.invoke(SlayerEvents.BossSpawn())
	}

	private fun onPacketReceive(event: PacketEvents.Receive) {
		if(!SkyBlockAPI.inSkyBlock) return

		val currentQuest = currentQuest ?: return
		if(currentQuest.entity != null) return

		val packet = event.packet as? EntityTrackerUpdateS2CPacket ?: return
		val ownerArmorStand = EntityUtils.getEntityById(packet.id) as? ArmorStandEntity ?: return

		val playerName = MCUtils.playerName ?: return
		val metadata = packet.trackedValues.associate { it.id to it.value }
		val armorStandName = metadata[2]?.let { (it as? Optional<*>)?.getOrNull() as? Text }?.string ?: return
		if(armorStandName != "Spawned by: $playerName") return

		val entity = EntityUtils.getNextEntity<LivingEntity>(ownerArmorStand, -3) ?: return
		if(entity.type != currentQuest.boss.entityType) return

		val armorStand = EntityUtils.getNextEntity<ArmorStandEntity>(ownerArmorStand, -2) ?: return
		val timerArmorStand = EntityUtils.getNextEntity<ArmorStandEntity>(ownerArmorStand, -1) ?: return

		currentQuest.apply {
			this.entity = entity
			this.armorStand = armorStand
			this.timerArmorStand = timerArmorStand
		}
	}

	private fun onEntityRender(event: EntityEvents.Render) {
		if(!SkyBlockAPI.inSkyBlock) return

		val currentQuest = currentQuest ?: return
		if(currentQuest.entity != null) return

		val entity = event.entity as? LivingEntity ?: return
		if(entity.type != currentQuest.boss.entityType) return
		if(entity in miniBosses) return

		val armorStand = EntityUtils.getNextEntity<ArmorStandEntity>(entity, 1) ?: return
		val armorStandName = armorStand.name.string

		if(armorStandName.contains(currentQuest.boss.displayName)) {
			val playerName = MCUtils.playerName ?: return
			val ownerArmorStand = EntityUtils.getNextEntity<ArmorStandEntity>(entity, 3) ?: return
			if(ownerArmorStand.name.string != "Spawned by: $playerName") return

			currentQuest.entity = entity
		} else if(currentQuest.boss.miniBossNames?.any { armorStandName.contains(it) } == true) {
			SlayerEvents.MINI_BOSS_SPAWN.invoke(SlayerEvents.MiniBossSpawn(entity))
			miniBosses.add(entity)
		}
	}

	// TODO: Use regexes as a slayer feature already has some of these
	private fun onChatMessage(message: String) {
		if(!SkyBlockAPI.inSkyBlock) return
		if(currentQuest == null) return

		when(message.trim()) {
			"SLAYER QUEST FAILED!", "Your Slayer Quest has been cancelled!" -> {
				SlayerEvents.QUEST_CLEAR.invoke(SlayerEvents.QuestClear())
				currentQuest = null
			}
			"SLAYER QUEST COMPLETE!", "NICE! SLAYER BOSS SLAIN!" -> {
				SlayerEvents.QUEST_CLEAR.invoke(SlayerEvents.QuestClear())
				SlayerEvents.BOSS_KILL.invoke(SlayerEvents.BossKill(currentQuest?.entity, currentQuest?.timerArmorStand))

				currentQuest?.apply {
					this.entity = null
					this.armorStand = null
					this.timerArmorStand = null
				}
			}
		}
	}

	private fun reset() {
		currentQuest = null
		miniBosses.clear()
	}

	data class SlayerQuest(
		val boss: SlayerBoss,
		var entity: LivingEntity? = null,
		var armorStand: ArmorStandEntity? = null,
		var timerArmorStand: ArmorStandEntity? = null,
		var spawned: Boolean = false
	)
}