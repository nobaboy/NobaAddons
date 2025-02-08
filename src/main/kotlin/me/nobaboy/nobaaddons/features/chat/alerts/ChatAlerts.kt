package me.nobaboy.nobaaddons.features.chat.alerts

import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.OptionGroup
import me.nobaboy.nobaaddons.config.option.Config
import me.nobaboy.nobaaddons.config.option.ConfigOption
import me.nobaboy.nobaaddons.config.utils.label
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

	override fun init() {
		listen(ChatMessageEvents.CHAT) { (message) ->
			val string = message.string.cleanFormatting()
			for(alert in allAlerts) {
				if(!alert.enabled || getKillSwitch(alert.id)) continue
				try {
					alert.process(string)
				} catch(ex: Throwable) {
					ErrorManager.logError(
						"${alert::class.simpleName} threw an error while processing a chat message", ex
					)
				}
			}
		}
	}

	override fun buildConfig(category: ConfigCategory.Builder) {
		if(killSwitch) return

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

	override fun saveEvent() {
	}
}