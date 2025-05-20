package me.nobaboy.nobaaddons.api.skyblock

import me.nobaboy.nobaaddons.core.slayer.SlayerBoss
import me.nobaboy.nobaaddons.events.impl.chat.ChatMessageEvents
import me.nobaboy.nobaaddons.events.impl.client.EntityEvents
import me.nobaboy.nobaaddons.events.impl.client.PacketEvents
import me.nobaboy.nobaaddons.events.impl.client.TickEvents
import me.nobaboy.nobaaddons.events.impl.skyblock.SkyBlockEvents
import me.nobaboy.nobaaddons.events.impl.skyblock.SlayerEvents
import me.nobaboy.nobaaddons.repo.Repo.fromRepo
import me.nobaboy.nobaaddons.utils.CommonPatterns
import me.nobaboy.nobaaddons.utils.RegexUtils.onFullMatch
import me.nobaboy.nobaaddons.utils.annotations.ApiModule
import me.nobaboy.nobaaddons.utils.collections.CollectionUtils.nextAfter
import me.nobaboy.nobaaddons.utils.mc.EntityUtils
import me.nobaboy.nobaaddons.utils.mc.MCUtils
import me.nobaboy.nobaaddons.utils.mc.ScoreboardUtils
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.decoration.ArmorStandEntity
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket
import net.minecraft.text.Text
import java.util.Optional
import kotlin.jvm.optionals.getOrNull

@ApiModule
object SlayerAPI {
	private val QUEST_FAILED_REGEX by Regex("^[ ]+SLAYER QUEST FAILED!").fromRepo("slayer.quest_failed")
	private val QUEST_CANCEL_MESSAGE by "Your Slayer Quest has been cancelled!".fromRepo("slayer.quest_cancel")

	var currentQuest: SlayerQuest? = null
		private set

	private val miniBosses = mutableListOf<LivingEntity>()

	init {
		SkyBlockEvents.ISLAND_CHANGE.register { reset() }
		TickEvents.TICK.register { onTick() }
		PacketEvents.POST_RECEIVE.register(this::onPacketReceive)
		EntityEvents.POST_RENDER.register(this::onEntityRender)
		ChatMessageEvents.CHAT.register(this::onChatMessage)
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
		if(previousState == false && currentQuest?.spawned == true) SlayerEvents.BOSS_SPAWN.dispatch(SlayerEvents.BossSpawn())
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

		val armorStand = EntityUtils.getNextEntity(entity, 1) as? ArmorStandEntity ?: return
		val armorStandName = armorStand.name.string

		if(currentQuest.boss.miniBossNames?.any { armorStandName.contains(it) } == true) {
			SlayerEvents.MINI_BOSS_SPAWN.dispatch(SlayerEvents.MiniBossSpawn(entity))
			miniBosses.add(entity)
		}
	}

	private fun onChatMessage(event: ChatMessageEvents.Chat) {
		if(!SkyBlockAPI.inSkyBlock) return

		val currentQuest = currentQuest ?: return
		val message = event.cleaned

		CommonPatterns.SLAYER_BOSS_SLAIN_REGEX.onFullMatch(message) {
			SlayerEvents.BOSS_KILL.dispatch(SlayerEvents.BossKill(currentQuest.entity, currentQuest.timerArmorStand))
			this@SlayerAPI.currentQuest = null
			return
		}

		CommonPatterns.SLAYER_QUEST_COMPLETE_REGEX.onFullMatch(message) {
			SlayerEvents.BOSS_KILL.dispatch(SlayerEvents.BossKill(currentQuest.entity, currentQuest.timerArmorStand))
			this@SlayerAPI.currentQuest?.apply {
				this.entity = null
				this.armorStand = null
				this.timerArmorStand = null
			}
			return
		}

		if(QUEST_FAILED_REGEX.matches(message) || message == QUEST_CANCEL_MESSAGE) {
			SlayerEvents.QUEST_CLEAR.dispatch(SlayerEvents.QuestClear())
			this@SlayerAPI.currentQuest = null
			return
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
		var spawned: Boolean = false,
	)
}