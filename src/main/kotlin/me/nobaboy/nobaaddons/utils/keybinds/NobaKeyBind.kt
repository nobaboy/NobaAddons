package me.nobaboy.nobaaddons.utils.keybinds

import me.nobaboy.nobaaddons.utils.CooldownManager
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import org.lwjgl.glfw.GLFW
import kotlin.time.Duration.Companion.milliseconds

open class NobaKeyBind(
	name: String,
	category: String,
	key: Int = GLFW.GLFW_KEY_UNKNOWN,
	inputType: InputUtil.Type,
	private val onPress: () -> Unit
) : KeyBinding(name, inputType, key, category) {
	private val cooldownManager = CooldownManager()

	constructor(name: String, onPress: () -> Unit) : this(
		name,
		"nobaaddons.name",
		GLFW.GLFW_KEY_UNKNOWN,
		InputUtil.Type.KEYSYM,
		onPress
	)

	@Suppress("UsePropertyAccessSyntax") // using property access syntax causes a StackOverflowError
	override fun setPressed(pressed: Boolean) {
		if(!super.isPressed() && pressed) {
			maybePress()
		}
		super.setPressed(pressed)
	}

	fun maybePress() {
		if(cooldownManager.isOnCooldown()) return
		cooldownManager.startCooldown(250.milliseconds)
		onPress()
	}
}