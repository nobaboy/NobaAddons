package me.nobaboy.nobaaddons.api.skyblock

import me.nobaboy.nobaaddons.core.slayer.SlayerBoss
import me.nobaboy.nobaaddons.events.EntityEvents
import me.nobaboy.nobaaddons.events.PacketEvents
import me.nobaboy.nobaaddons.events.skyblock.SlayerEvents
import me.nobaboy.nobaaddons.utils.CollectionUtils.nextAfter
import me.nobaboy.nobaaddons.utils.EntityUtils
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.ScoreboardUtils
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.decoration.ArmorStandEntity
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket

object SlayerAPI {
	var currentQuest: SlayerQuest? = null
		private set

	private val miniBosses = mutableSetOf<LivingEntity>()

	fun init() {
		PacketEvents.POST_RECEIVE.register(this::onPacketReceive)
		EntityEvents.POST_RENDER.register(this::onEntityRender)
//		ChatMessageEvents.CHAT.register { (message) -> onChatMessage(message.string.cleanFormatting()) }
		ClientTickEvents.END_CLIENT_TICK.register { onTick() }
		ClientReceiveMessageEvents.GAME.register { message, _ -> onChatMessage(message.string.cleanFormatting()) }
	}

	private fun onPacketReceive(event: PacketEvents.Receive) {
		if(!SkyBlockAPI.inSkyBlock) return

		val currentQuest = currentQuest ?: return
		if(currentQuest.entity != null) return

		val packet = event.packet as? EntityTrackerUpdateS2CPacket ?: return
		val armorStand = EntityUtils.getEntityById(packet.id) as? ArmorStandEntity ?: return

		val entity = EntityUtils.getNextEntity(armorStand, -1) as? LivingEntity ?: return
		if(entity.type != currentQuest.boss.entityType) return

		val ownerArmorStand = EntityUtils.getNextEntity(armorStand, 2) as? ArmorStandEntity ?: return
		val playerName = MCUtils.playerName ?: return
		if(ownerArmorStand.name.string != "Spawned by: $playerName") return

		val timerArmorStand = EntityUtils.getNextEntity(armorStand, 1) as? ArmorStandEntity ?: return

		currentQuest.apply {
			this.entity = entity
			this.armorStand = armorStand
			this.timerArmorStand = timerArmorStand
		}
	}

	private fun onEntityRender(event: EntityEvents.Render) {
		if(!SkyBlockAPI.inSkyBlock) return

		val entity = event.entity as? LivingEntity ?: return
		if(entity is ArmorStandEntity) return
		if(entity in miniBosses) return

		val currentQuest = this.currentQuest ?: return
		if(entity.type != currentQuest.boss.entityType) return
		val armorStand = EntityUtils.getNextEntity(entity, 1) as? ArmorStandEntity ?: return

		if(currentQuest.boss.miniBossType?.names?.any { armorStand.name.string.contains(it) } == true) {
			SlayerEvents.MINI_BOSS_SPAWN.invoke(SlayerEvents.MiniBossSpawn(entity))
			miniBosses.add(entity)
		}
	}

	private fun onChatMessage(message: String) {
		println(message)

		if(!SkyBlockAPI.inSkyBlock) return
		if(currentQuest == null) return

		when(message.trim()) {
			"SLAYER QUEST FAILED!", "Your Slayer Quest has been cancelled!" -> currentQuest = null
			"SLAYER QUEST COMPLETE!", "NICE! SLAYER BOSS SLAIN!" -> {
				SlayerEvents.BOSS_KILL.invoke(SlayerEvents.BossKill(currentQuest?.entity, currentQuest?.timerArmorStand))
				currentQuest = null
			}
		}
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

	data class SlayerQuest(
		val boss: SlayerBoss,
		var entity: LivingEntity? = null,
		var armorStand: ArmorStandEntity? = null,
		var timerArmorStand: ArmorStandEntity? = null,
		var spawned: Boolean = false
	)
}