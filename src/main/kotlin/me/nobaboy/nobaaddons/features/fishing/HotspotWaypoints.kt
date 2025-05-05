package me.nobaboy.nobaaddons.features.fishing

import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.core.SkyBlockStat
import me.nobaboy.nobaaddons.events.impl.client.EntityEvents
import me.nobaboy.nobaaddons.events.impl.skyblock.SkyBlockEvents
import me.nobaboy.nobaaddons.utils.mc.EntityUtils
import me.nobaboy.nobaaddons.utils.mc.MCUtils
import me.nobaboy.nobaaddons.utils.StringUtils
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import me.nobaboy.nobaaddons.utils.TextUtils.blue
import me.nobaboy.nobaaddons.utils.TextUtils.buildText
import me.nobaboy.nobaaddons.utils.TextUtils.hoverText
import me.nobaboy.nobaaddons.utils.TextUtils.toText
import me.nobaboy.nobaaddons.utils.TextUtils.yellow
import me.nobaboy.nobaaddons.utils.Timestamp
import me.nobaboy.nobaaddons.utils.TimeUtils.toShortString
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import me.nobaboy.nobaaddons.utils.chat.ChatUtils.clickAction
import me.nobaboy.nobaaddons.utils.chat.HypixelCommands
import me.nobaboy.nobaaddons.utils.render.RenderUtils
import me.nobaboy.nobaaddons.utils.toNobaVec
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.minecraft.entity.decoration.ArmorStandEntity
import net.minecraft.text.Text
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

object HotspotWaypoints {
	private val config get() = NobaConfig.fishing
	private val enabled: Boolean get() = config.hotspotWaypoints && SkyBlockAPI.inSkyBlock

	private val hotspots = mutableListOf<Hotspot>()

	fun init() {
		SkyBlockEvents.ISLAND_CHANGE.register { hotspots.clear() }
		EntityEvents.POST_RENDER.register(this::onEntityRender)
		WorldRenderEvents.AFTER_TRANSLUCENT.register(this::renderWaypoints)
	}

	private fun onEntityRender(event: EntityEvents.Render) {
		if(!enabled) return

		val armorStand = event.entity as? ArmorStandEntity ?: return
		if(hotspots.any { it.armorStand == armorStand }) return

		if(MCUtils.player?.canSee(armorStand) != true) return
		if(armorStand.name.string != "HOTSPOT") return

		val statArmorStand = EntityUtils.getNextEntity<ArmorStandEntity>(armorStand, 1) ?: return
		val stat = SkyBlockStat.getByName(statArmorStand.name.string.cleanFormatting()) ?: return

		val timestamp = Timestamp.now() + 4.5.minutes - (armorStand.age / 20).seconds
		val hotspot = Hotspot(armorStand, stat, timestamp)

		val message = compileMessage(hotspot)
		ChatUtils.addMessage(message)

		hotspots.add(hotspot)
	}

	private fun renderWaypoints(context: WorldRenderContext) {
		if(!enabled) return

		hotspots.removeIf { !it.armorStand.isAlive }
		hotspots.forEach {
			val time = it.remainingTime.ifEmpty { "Soon" }

			RenderUtils.renderBeaconBeam(context, it.location, it.stat.color)
			RenderUtils.renderText(context, it.location.center().raise(1.5), time, throughBlocks = true)
		}
	}

	private fun compileMessage(hotspot: Hotspot): Text = buildText {
		val (x, y, z) = hotspot.location.toDoubleArray()
		val randomString = StringUtils.randomAlphanumeric()

		val stat = hotspot.stat
		val announceMessage = "x: $x, y: $y, z: $z | ${stat.prefixedName} Hotspot @$randomString"

		append("Found a ")
		append(stat.displayName)
		append(" Hotspot! ")
		append("[Share in All Chat]".toText()
			.yellow()
			.clickAction { HypixelCommands.allChat(announceMessage) }
			.hoverText("Click to send in All Chat!")
		)
		append(" ")
		append("[Share in Party Chat]".toText()
			.blue()
			.clickAction { HypixelCommands.partyChat(announceMessage, partyCheck = false) }
			.hoverText("Click to send in Party Chat!")
		)
	}

	private data class Hotspot(val armorStand: ArmorStandEntity, val stat: SkyBlockStat, val timestamp: Timestamp) {
		val location = armorStand.pos.toNobaVec().lower(2).roundToBlock()

		val remainingTime: String get() = timestamp.timeRemaining().toShortString()
	}
}