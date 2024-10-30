package me.nobaboy.nobaaddons.utils.keybinds

import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import net.minecraft.client.util.InputUtil
import org.lwjgl.glfw.GLFW

class CommandKeyBind(
	name: String,
	command: String,
	category: String = "nobaaddons.name",
	key: Int = GLFW.GLFW_KEY_UNKNOWN,
	inputType: InputUtil.Type = InputUtil.Type.KEYSYM
) : NobaKeyBind(name, category, key, inputType, { ChatUtils.queueCommand(command) })