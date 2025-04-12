package me.nobaboy.nobaaddons.core

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.events.impl.client.TickEvents
import me.nobaboy.nobaaddons.repo.Repo
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.Scheduler
import me.nobaboy.nobaaddons.utils.TextUtils.appendLine
import me.nobaboy.nobaaddons.utils.TextUtils.aqua
import me.nobaboy.nobaaddons.utils.TextUtils.buildLiteral
import me.nobaboy.nobaaddons.utils.TextUtils.buildText
import me.nobaboy.nobaaddons.utils.TextUtils.gold
import me.nobaboy.nobaaddons.utils.TextUtils.gray
import me.nobaboy.nobaaddons.utils.TextUtils.openUrl
import me.nobaboy.nobaaddons.utils.TextUtils.underline
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import me.nobaboy.nobaaddons.utils.tr
import net.fabricmc.loader.api.Version

object UpdateNotifier {
	private val config get() = NobaConfig.general

	private var inWorld: Boolean = false
	private var notifiedUpdate: Boolean = false

	private val CURRENT = NobaAddons.VERSION_INFO
	private val UPDATE_INFO by Repo.create<UpdateInfo>("update.json")

	fun init() {
		TickEvents.TICK.register {
			if(MCUtils.world != null && !inWorld) {
				onJoin()
				inWorld = true
			} else if(inWorld && MCUtils.world == null) {
				inWorld = false
			}
		}
	}

	private fun onJoin() {
		if(MCUtils.player == null) return
		if(notifiedUpdate) return
		if(!config.updateNotifier) return
		val update = UPDATE_INFO ?: return
		if(update.latest > CURRENT) {
			notifiedUpdate = true
			Scheduler.schedule(10) { sendUpdateNotification() }
		}
	}

	fun sendUpdateNotification() {
		val update = UPDATE_INFO ?: return
		if(MCUtils.VERSION_INFO !in update.forMinecraft) return

		val message = buildText {
			appendLine()
			append(NobaAddons.PREFIX)
			append(tr("nobaaddons.updateAvailable", "A new update is available: ${update.latest}:").gold())
			appendLine()
			appendLine(buildLiteral(update.releaseNotes.prependIndent(" ")) { gray() })
			append(tr("nobaaddons.updateAvailable.download", "Click here to download the update from Modrinth")
				.aqua()
				.openUrl("https://modrinth.com/mod/nobaaddons/versions")
				.underline())
			appendLine()
		}

		ChatUtils.addMessage(message, prefix = false)
	}

	@Serializable
	private data class UpdateInfo(
		@Serializable(with = VersionSerializer::class) val latest: Version,
		val releaseNotes: String,
		val forMinecraft: List<@Serializable(with = VersionSerializer::class) Version>,
	)

	private class VersionSerializer : KSerializer<Version> {
		override val descriptor: SerialDescriptor =
			PrimitiveSerialDescriptor("me.nobaboy.nobaaddons.core.UpdateNotifier\$SemanticVersionSerializer", PrimitiveKind.STRING)

		override fun serialize(encoder: Encoder, value: Version) {
			encoder.encodeString(value.toString())
		}

		override fun deserialize(decoder: Decoder): Version = Version.parse(decoder.decodeString())
	}
}