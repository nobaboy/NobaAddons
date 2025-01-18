package me.nobaboy.nobaaddons.features.ui.infobox

import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.features.ui.infobox.functions.FunctionsManager
import me.nobaboy.nobaaddons.screens.infoboxes.InfoBoxesScreen
import me.nobaboy.nobaaddons.ui.TextHudElement
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.TextUtils.toText
import me.nobaboy.nobaaddons.utils.tr
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import net.minecraft.util.Formatting

private val COLOR_REGEX = Regex("&([0-9a-fk-orz])", RegexOption.IGNORE_CASE)

class InfoBoxHudElement(val textElement: InfoBoxElement) : TextHudElement(textElement) {
	override val name: Text = tr("nobaaddons.ui.infoBox", "Info Box")
	override val size: Pair<Int, Int> get() = getBoundsFrom(text)

	override val enabled: Boolean
		get() = SkyBlockAPI.inSkyBlock || NobaConfig.INSTANCE.uiAndVisuals.renderInfoBoxesOutsideSkyBlock

	override fun shouldRender(): Boolean =
		super.shouldRender() && MCUtils.client.currentScreen !is InfoBoxesScreen

	val text: List<Text> get() =
		textElement.text.replace("\\n", "\n").split("\n").map { compileText(it).toText() }

	override fun renderText(context: DrawContext) {
		renderLines(context, text)
	}

	private fun compileText(text: String): String {
		val result = FunctionsManager.processText(text)
		return result.replace(COLOR_REGEX, "${Formatting.FORMATTING_CODE_PREFIX}$1")
	}
}