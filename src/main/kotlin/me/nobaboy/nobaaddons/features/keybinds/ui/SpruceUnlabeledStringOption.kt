package me.nobaboy.nobaaddons.features.keybinds.ui

import dev.lambdaurora.spruceui.Position
import dev.lambdaurora.spruceui.option.SpruceStringOption
import dev.lambdaurora.spruceui.widget.SpruceWidget
import dev.lambdaurora.spruceui.widget.text.SpruceTextFieldWidget
import net.minecraft.text.Text
import java.util.function.Consumer
import java.util.function.Supplier

class SpruceUnlabeledStringOption(key: String, getter: Supplier<String>, setter: Consumer<String>) : SpruceStringOption(key, getter, setter, null, null) {
	override fun createWidget(position: Position, width: Int): SpruceWidget {
		return SpruceTextFieldWidget(position, width, 20, Text.empty()).apply {
			text = get()
			changedListener = Consumer<String> { set(it) }
		}
	}
}
