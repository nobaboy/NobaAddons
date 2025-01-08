package me.nobaboy.nobaaddons.features.general

import com.google.gson.GsonBuilder
import com.mojang.serialization.JsonOps
import me.nobaboy.nobaaddons.data.PersistentCache
import me.nobaboy.nobaaddons.mixins.accessors.HandledScreenAccessor
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.StringUtils.stripWhitespace
import me.nobaboy.nobaaddons.utils.annotations.UntranslatedMessage
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import org.lwjgl.glfw.GLFW

object DevFeatures {
	private val gson = GsonBuilder().setPrettyPrinting().serializeNulls().create()

	@JvmStatic
	@OptIn(UntranslatedMessage::class)
	fun copyCurrentHoveredInventorySlot(screen: HandledScreen<out ScreenHandler>) {
		val slot = (screen as HandledScreenAccessor).focusedSlot ?: return
		if(slot.stack.isEmpty) return

		MCUtils.copyToClipboard(buildTextToCopy(slot.stack))
		ChatUtils.addMessage("Copied item data to clipboard")
	}

	private fun buildTextToCopy(item: ItemStack): String = buildString {
		append("---- DATA COMPONENTS ----\n\n")
		// ItemStack#hasChangedComponent(ComponentType<*>) doesn't exist on 1.21.1
		item.components.filter { item.defaultComponents[it.type] !== it.value }.forEach {
			appendLine("${it.type}:")
			appendLine(it.value.toString().prependIndent("  "))
			appendLine()
		}
		append("---- SERIALIZED ITEM ----\n\n")
		val encoded = ItemStack.CODEC.encodeStart(MCUtils.player!!.registryManager.getOps(JsonOps.INSTANCE), item).orThrow
		append(gson.toJson(encoded))
	}.stripWhitespace()

	@JvmStatic
	fun shouldCopy(keyCode: Int): Boolean {
		return PersistentCache.devMode && keyCode == GLFW.GLFW_KEY_RIGHT_CONTROL
	}
}