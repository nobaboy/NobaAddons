package me.nobaboy.nobaaddons.screens.keybinds

import dev.lambdaurora.spruceui.Position
import dev.lambdaurora.spruceui.SpruceTexts
import dev.lambdaurora.spruceui.option.SpruceSeparatorOption
import dev.lambdaurora.spruceui.option.SpruceSimpleActionOption
import dev.lambdaurora.spruceui.screen.SpruceScreen
import dev.lambdaurora.spruceui.widget.SpruceButtonWidget
import dev.lambdaurora.spruceui.widget.container.SpruceOptionListWidget
import me.nobaboy.nobaaddons.features.keybinds.data.KeybindConfig
import me.nobaboy.nobaaddons.features.keybinds.data.KeybindConfig.Keybind
import me.nobaboy.nobaaddons.utils.TextUtils.buildText
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.InputUtil
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import org.lwjgl.glfw.GLFW

class KeybindsConfigScreen(private val parent: Screen?) : SpruceScreen(Text.translatable("nobaaddons.keybinds")) {
	private var selecting: Selecting? = null
	private lateinit var options: SpruceOptionListWidget
	private val keybinds by KeybindConfig::keybinds

	override fun init() {
		super.init()
		initList()
		addDrawableChild(options)
		addDrawableChild(SpruceButtonWidget(Position.of(this, width / 2 - 155, height - 29), 150, 20, Text.translatable("nobaaddons.keybind.add")) {
			keybinds.add(Keybind())
			clearAndInit()
		})
		addDrawableChild(SpruceButtonWidget(Position.of(this, width / 2 - 155 + 160, height - 29), 150, 20, SpruceTexts.GUI_DONE) { close() })
	}

	private fun initList() {
		options = SpruceOptionListWidget(Position.of(0, 22), width, height - 35 - 22)
		keybinds.forEachIndexed { i, keybind ->
			val factory = SpruceSimpleActionOption.ButtonFactory { position, width, message, action ->
				SpruceButtonWidget(position, width, 20, keyText(keybind.keycode), action)
			}
			options.addOptionEntry(
				// TODO this ui kinda sucks in that its hard to explain that you shouldn't add a / to the command,
				// 		but oh well, I don't feel like making something better.
				SpruceUnlabeledStringOption("nobaaddons.keybind.command", { keybind.command }, { keybind.command = it }),
				SpruceSimpleActionOption("nobaaddons.keybind.key", factory) { select(keybind, it) }
			)
			options.addSmallSingleOptionEntry(SpruceSimpleActionOption.of("nobaaddons.keybind.remove") {
				keybinds.remove(keybind)
				clearAndInit()
			})
			if(keybinds.size - i > 1) {
				options.addSingleOptionEntry(SpruceSeparatorOption("", false, null))
			}
		}
	}

	private fun select(keybind: Keybind, button: SpruceButtonWidget) {
		if(selecting != null) return
		selecting = Selecting(keybind, button)
		button.message = buildText {
			append(Text.literal(" > ").formatted(Formatting.YELLOW))
			append(button.message)
			append(Text.literal(" <").formatted(Formatting.YELLOW))
		}
	}

	override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
		selecting?.let {
			val key = when(keyCode) {
				GLFW.GLFW_KEY_ESCAPE -> GLFW.GLFW_KEY_UNKNOWN
				else -> keyCode
			}
			selecting = null
			it.keybind.keycode = key
			it.button.message = keyText(key)
			return true
		}
		return super.keyPressed(keyCode, scanCode, modifiers)
	}

	private fun keyText(key: Int): Text = when(key) {
		0 -> SpruceTexts.NOT_BOUND
		else -> InputUtil.Type.KEYSYM.createFromCode(key).localizedText
	}

	override fun close() {
		keybinds.removeIf { it.command.isBlank() }
		KeybindConfig.save()
		client!!.setScreen(parent)
	}

	private class Selecting(val keybind: Keybind, val button: SpruceButtonWidget)
}
