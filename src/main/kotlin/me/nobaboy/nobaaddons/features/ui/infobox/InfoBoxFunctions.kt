package me.nobaboy.nobaaddons.features.ui.infobox

import me.nobaboy.nobaaddons.api.SkyblockAPI
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.Utils
import kotlin.math.sqrt

// TODO: Make it a function system rather than using an enum
enum class InfoBoxFunctions(val aliases: List<String>, val runnable: () -> String) {
	// Player Info Functions
	POS_X(listOf("{posX}", "{locX}"), { getPosX() }),
	POS_Y(listOf("{posY}", "{locY}"), { getPosY() }),
	POS_Z(listOf("{posZ}", "{locZ}"), { getPosZ() }),
	PITCH(listOf("{pitch}"), { getPitch() }),
	YAW(listOf("{yaw}"), { getYaw() }),

	// Info Functions
	FPS(listOf("{fps}"), { getFPS() }),
	BPS(listOf("{bps}"), { getBPS() }),
	PING(listOf("{ping}"), { getPing() }),
//	TPS(listOf("{tps}"), { getTPS() }),

	// Scoreboard Functions
	PURSE(listOf("{purse}"), { getPurse() }),
	BITS(listOf("{bits}"), { getBits() });

	companion object {
		val client = MCUtils.client

		fun getPosX(): String = String.format("%.1f", client.player?.x ?: 0.0)
		fun getPosY(): String = String.format("%.1f", client.player?.y ?: 0.0)
		fun getPosZ(): String = String.format("%.1f", client.player?.z ?: 0.0)
		fun getBPS(): String {
			val player = client.player ?: return "NaN"
			val dX = player.x - player.prevX
			val dY = player.y - player.prevY
			val dZ = player.z - player.prevZ
			val bps = sqrt((dX * dX) + (dY * dY) + (dZ * dZ)) * 20
			return String.format("%.1f", bps)
		}

		fun getPitch(): String = String.format("%.1f", client.player?.pitch ?: 0.0)
		fun getYaw(): String {
			val yaw = client.player?.yaw ?: 0.0f
			val normalizedYaw = (yaw + 180) % 360 - 180
			return String.format("%.1f", normalizedYaw)
		}

		fun getFPS(): String = client.currentFps.toString()

//		fun getTPS(): String =
		fun getPing(): String = Utils.ping.toString()

		fun getPurse(): String = String.format("%,d", SkyblockAPI.purse)
		fun getBits(): String = String.format("%,d", SkyblockAPI.bits)
	}
}
