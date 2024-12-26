package me.nobaboy.nobaaddons.screens.keybinds.impl

import me.nobaboy.nobaaddons.utils.CooldownManager
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.text.Text
import net.minecraft.text.TranslatableTextContent
import org.lwjgl.glfw.GLFW
import kotlin.time.Duration.Companion.milliseconds

open class NobaKeyBind(
	name: String,
	category: String = "nobaaddons.name",
	key: Int = GLFW.GLFW_KEY_UNKNOWN,
	inputType: InputUtil.Type = InputUtil.Type.KEYSYM,
	private val onPress: () -> Unit
) : KeyBinding(name, inputType, key, category) {
	private val cooldownManager = CooldownManager()

	/**
	 * Override to allow for mc-auto-translations to automatically discover translation strings
	 * without needing to manually register it
	 */
	constructor(
		name: Text,
		category: String = "nobaaddons.name",
		key: Int = GLFW.GLFW_KEY_UNKNOWN,
		inputType: InputUtil.Type = InputUtil.Type.KEYSYM,
		onPress: () -> Unit,
	) : this(
		(name.content as TranslatableTextContent).key,
		category,
		key,
		inputType,
		onPress,
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