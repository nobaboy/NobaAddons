package me.nobaboy.nobaaddons.features.chat.channeldisplay

import kotlinx.serialization.Serializable
import me.nobaboy.nobaaddons.api.PartyAPI
import me.nobaboy.nobaaddons.utils.TextUtils.aqua
import me.nobaboy.nobaaddons.utils.TextUtils.buildText
import me.nobaboy.nobaaddons.utils.TextUtils.gold
import me.nobaboy.nobaaddons.utils.TextUtils.gray
import me.nobaboy.nobaaddons.utils.TextUtils.red
import me.nobaboy.nobaaddons.utils.TextUtils.toText
import me.nobaboy.nobaaddons.utils.TextUtils.withColor
import me.nobaboy.nobaaddons.utils.TextUtils.yellow
import me.nobaboy.nobaaddons.utils.Timestamp
import me.nobaboy.nobaaddons.utils.Timestamp.Companion.toShortString
import me.nobaboy.nobaaddons.utils.tr
import net.minecraft.text.Text

@Serializable
data class ActiveChatChannel(val channel: ChatChannel, val dmWith: String? = null, var expires: Timestamp? = null) {
	val name: Text get() = when(channel) {
		ChatChannel.DM -> dmWith?.toText()?.gold() ?: channel.toText()
		else -> channel.name.toText().withColor(channel.color)
	}

	val extraInfo: Text? get() = when(channel) {
		ChatChannel.PARTY -> buildPartyText()
		ChatChannel.DM -> buildDmText()
		else -> null
	}

	private inline fun buildExtraInfo(crossinline info: () -> Text) = buildText {
		append("(")
		append(info())
		append(")")
		gray()
	}

	private fun buildPartyText(): Text = buildExtraInfo {
		val party = PartyAPI.party
		when {
			party == null -> tr("nobaaddons.chat.channel.emptyParty", "No party!").red()
			else -> tr("nobaaddons.chat.channel.partyCount", "${party.members.size} in party").aqua()
		}
	}

	private fun buildDmText(): Text = buildExtraInfo {
		val expires = this@ActiveChatChannel.expires ?: Timestamp.distantPast()
		val remaining = expires.timeRemaining()
		when {
			remaining.isNegative() -> tr("nobaaddons.chat.channel.dmExpired", "Expired!").red()
			else -> remaining.toShortString().toText().yellow()
		}
	}
}
