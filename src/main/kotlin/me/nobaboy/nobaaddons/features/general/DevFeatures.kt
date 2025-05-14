package me.nobaboy.nobaaddons.features.general

import me.nobaboy.nobaaddons.core.PersistentCache
import me.nobaboy.nobaaddons.mixins.accessors.HandledScreenAccessor
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.annotations.UntranslatedMessage
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.visitor.StringNbtWriter
import net.minecraft.screen.ScreenHandler
import org.lwjgl.glfw.GLFW

object DevFeatures {
	@JvmStatic
	@OptIn(UntranslatedMessage::class)
	fun copyCurrentHoveredInventorySlot(screen: HandledScreen<out ScreenHandler>) {
		val slot = (screen as HandledScreenAccessor).focusedSlot ?: return
		if(slot.stack.isEmpty) return

		MCUtils.copyToClipboard(buildTextToCopy(slot.stack))
		ChatUtils.addMessage("Copied item data to clipboard")
	}

	private fun buildTextToCopy(item: ItemStack): String {
		val encoded = ItemStack.CODEC.encodeStart(MCUtils.player!!.registryManager.getOps(NbtOps.INSTANCE), item).orThrow
		return StringNbtWriter().apply(encoded)
	}

	@JvmStatic
	fun shouldCopy(keyCode: Int): Boolean {
		return PersistentCache.devMode && keyCode == GLFW.GLFW_KEY_RIGHT_CONTROL
	}
}