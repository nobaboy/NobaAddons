package me.nobaboy.nobaaddons.features.ui.infobox

import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.features.ui.infobox.functions.FunctionsManager
import me.nobaboy.nobaaddons.screens.infoboxes.InfoBoxesScreen
import me.nobaboy.nobaaddons.ui.TextHudElement
import me.nobaboy.nobaaddons.utils.CommonPatterns
import me.nobaboy.nobaaddons.utils.mc.MCUtils
import me.nobaboy.nobaaddons.utils.TextUtils.toText
import me.nobaboy.nobaaddons.utils.properties.CacheFor
import me.nobaboy.nobaaddons.utils.tr
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import kotlin.time.Duration.Companion.seconds

class InfoBoxHudElement(val textElement: InfoBoxElement) : TextHudElement(textElement) {
	override val name: Text = tr("nobaaddons.ui.infoBox", "Info Box")
	override val size: Pair<Int, Int> get() = getBoundsFrom(text)
	override val dynamicScaling: Boolean = true

	override val enabled: Boolean
		get() = SkyBlockAPI.inSkyBlock || NobaConfig.uiAndVisuals.renderInfoBoxesOutsideSkyBlock

	override fun shouldRender(): Boolean =
		super.shouldRender() && MCUtils.client.currentScreen !is InfoBoxesScreen

	val text: List<Text> by CacheFor(0.25.seconds) {
		textElement.text.replace("\\n", "\n").split("\n").map { compileText(it).toText() }
	}

	override fun renderText(context: DrawContext) {
		renderLines(context, text)
	}

	private fun compileText(text: String): String {
		val result = FunctionsManager.processText(text)
		return result.replace(CommonPatterns.COLOR_REGEX, "${Formatting.FORMATTING_CODE_PREFIX}$1")
	}
}