package me.nobaboy.nobaaddons.features.chat

import dev.isxander.yacl3.api.NameableEnum
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.mixins.accessors.ChatHudAccessor
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import me.nobaboy.nobaaddons.utils.tr
import net.minecraft.client.gui.hud.ChatHudLine
import net.minecraft.client.gui.screen.Screen
import java.lang.ref.WeakReference
import java.util.WeakHashMap

object CopyChatFeature {
	@get:JvmStatic val messages = WeakHashMap<ChatHudLine.Visible, WeakReference<ChatHudLine>>(200)

	private val config get() = NobaConfig.INSTANCE.chat.copyChat

	private fun isEnabled(button: Int): Boolean {
		if(!config.enabled) return false
		return when(config.mode) {
			CopyWith.RIGHT_CLICK -> button == 1
			CopyWith.CTRL_CLICK -> Screen.hasControlDown()
		}
	}

	@JvmStatic
	fun copy(button: Int, mouseX: Double, mouseY: Double): Boolean {
		if(!isEnabled(button)) return false

		val hud = MCUtils.client.inGameHud.chatHud as ChatHudAccessor
		val index = hud.callGetMessageIndex(hud.callToChatLineX(mouseX), hud.callToChatLineY(mouseY))
		if(index == -1) return false

		val visibleLine = hud.visibleMessages[index]
		val cleaned = messages[visibleLine]?.get()?.content?.string?.cleanFormatting() ?: return false

		MCUtils.copyToClipboard(cleaned)
		ChatUtils.addMessage(tr("nobaaddons.chat.copiedMessage", "Copied chat message to clipboard"))
		return true
	}

	enum class CopyWith : NameableEnum {
		RIGHT_CLICK,
		CTRL_CLICK,
		;

		override fun getDisplayName() = when(this) {
			RIGHT_CLICK -> tr("nobaaddons.label.copyButton.rightClick", "Right Click")
			CTRL_CLICK -> tr("nobaaddons.label.copyButton.ctrlClick", "Control Held")
		}
	}
}