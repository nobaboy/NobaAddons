package me.nobaboy.nobaaddons.utils.keybinds

import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import net.minecraft.client.util.InputUtil
import org.lwjgl.glfw.GLFW

class CommandKeyBind(
	name: String,
	category: String,
	key: Int = GLFW.GLFW_KEY_UNKNOWN,
	inputType: InputUtil.Type,
	command: String
) : NobaKeyBind(name, category, key, inputType, { ChatUtils.sendCommand(command) }) {
	constructor(name: String, command: String) : this(name, "nobaaddons.name", GLFW.GLFW_KEY_UNKNOWN, InputUtil.Type.KEYSYM, command)
}