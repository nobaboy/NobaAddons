package me.nobaboy.nobaaddons.features.general

import com.google.gson.GsonBuilder
import com.mojang.serialization.JsonOps
import me.nobaboy.nobaaddons.core.PersistentCache
import me.nobaboy.nobaaddons.mixins.accessors.HandledScreenAccessor
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.annotations.UntranslatedMessage
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import org.lwjgl.glfw.GLFW

object DevFeatures {
	private val gson = GsonBuilder().setPrettyPrinting().create()

	@JvmStatic
	@OptIn(UntranslatedMessage::class)
	fun copyCurrentHoveredInventorySlot(screen: HandledScreen<out ScreenHandler>) {
		val slot = (screen as HandledScreenAccessor).focusedSlot ?: return
		if(slot.stack.isEmpty) return

		// it's easier to just dump the item to json than to try to use any of minecraft's nbt pretty printing
		val encoded = ItemStack.CODEC.encodeStart(MCUtils.player!!.registryManager.getOps(JsonOps.INSTANCE), slot.stack).orThrow
		MCUtils.copyToClipboard(gson.toJson(encoded))
		ChatUtils.addMessage("Copied item data to clipboard")
	}

	@JvmStatic
	fun shouldCopy(keyCode: Int): Boolean {
		return PersistentCache.devMode && keyCode == GLFW.GLFW_KEY_RIGHT_CONTROL
	}
}