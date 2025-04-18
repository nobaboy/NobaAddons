package me.nobaboy.nobaaddons.features.chat.channeldisplay

import kotlinx.serialization.Serializable
import me.nobaboy.nobaaddons.utils.TextUtils.buildText
import me.nobaboy.nobaaddons.utils.TextUtils.gold
import me.nobaboy.nobaaddons.utils.TextUtils.gray
import me.nobaboy.nobaaddons.utils.TextUtils.green
import me.nobaboy.nobaaddons.utils.TextUtils.red
import me.nobaboy.nobaaddons.utils.TextUtils.toText
import me.nobaboy.nobaaddons.utils.TextUtils.withColor
import me.nobaboy.nobaaddons.utils.Timestamp
import me.nobaboy.nobaaddons.utils.Timestamp.Companion.toShortString
import me.nobaboy.nobaaddons.utils.tr
import net.minecraft.text.Text
import kotlin.time.Duration.Companion.seconds

@Serializable
data class ActiveChatChannel(val channel: ChatChannel, val dmWith: String? = null, var expires: Timestamp? = null) {
	fun toText(): Text = when(channel) {
		ChatChannel.DM -> buildDmText()
		else -> channel.name.toText().withColor(channel.color)
	}

	private fun buildDmText(): Text {
		if(dmWith == null) {
			return ChatChannel.DM.name.toText().gold()
		}
		val expires = this@ActiveChatChannel.expires ?: Timestamp.distantPast()
		return buildText {
			append(dmWith.toText().gold())
			append(buildText {
				append(" (")
				// < 1 second is *technically* incorrect for that one second, but it's easier than fixing this short
				// format to properly display 0s in such a case
				if(expires.timeRemaining() < 1.seconds) {
					append(tr("nobaaddons.chat.channel.dm.expired", "Expired").red())
				} else {
					append(expires.timeRemaining().toShortString().toText().green())
				}
				append(")")
				gray()
			})
		}
	}
}
