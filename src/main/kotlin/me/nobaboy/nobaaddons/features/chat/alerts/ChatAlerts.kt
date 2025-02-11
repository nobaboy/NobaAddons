package me.nobaboy.nobaaddons.features.chat.alerts

import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.OptionGroup
import me.nobaboy.nobaaddons.config.option.Config
import me.nobaboy.nobaaddons.config.option.ConfigOption
import me.nobaboy.nobaaddons.config.utils.label
import me.nobaboy.nobaaddons.events.EventListener
import me.nobaboy.nobaaddons.events.impl.chat.ChatMessageEvents
import me.nobaboy.nobaaddons.features.Feature
import me.nobaboy.nobaaddons.features.FeatureCategory
import me.nobaboy.nobaaddons.features.chat.alerts.crimsonisle.MythicSeaCreatureAlert
import me.nobaboy.nobaaddons.features.chat.alerts.crimsonisle.VanquisherAlert
import me.nobaboy.nobaaddons.utils.ErrorManager
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import me.nobaboy.nobaaddons.utils.tr
import net.minecraft.text.Text

object ChatAlerts : Feature("chatAlerts", tr("nobaaddons.feature.chatAlerts", "Alerts"), FeatureCategory.CHAT) {
	private val alerts = buildMap<Text, List<ChatAlert>> {
		put(tr("nobaaddons.feature.chatAlerts.crimsonIsle", "Crimson Isle"), listOf(
			MythicSeaCreatureAlert,
			VanquisherAlert
		))
	}

	private val allAlerts by lazy { alerts.values.flatMap { it } }

	override val options: Map<String, Config> = buildMap {
		putAll(allAlerts.map { it.id to it })
	}

	@EventListener
	private fun onChatMessage(event: ChatMessageEvents.Chat) {
		val string = event.message.string.cleanFormatting()
		for(alert in allAlerts) {
			if(!alert.enabled) continue
			try {
				alert.process(string)
			} catch(ex: Throwable) {
				ErrorManager.logError(
					"${alert::class.simpleName} threw an error while processing a chat message", ex
				)
			}
		}
	}

	override fun buildConfig(category: ConfigCategory.Builder) {
		deepBuildYaclOptions()
		category.group(OptionGroup.createBuilder().apply {
			name(name)
			collapsed(true)
			alerts.forEach { (group, options) ->
				label(group)
				options(
					options
						.flatMap { it.options.values }
						.mapNotNull { (it as? ConfigOption<*>)?.yaclOption }
				)
			}
		}.build())
	}
}