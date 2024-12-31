package me.nobaboy.nobaaddons.config

import dev.isxander.yacl3.config.v2.api.SerialEntry
import me.nobaboy.nobaaddons.config.configs.*

class NobaConfig {
	@SerialEntry
	val version: Int = NobaConfigManager.CONFIG_VERSION

	@SerialEntry
	val general: GeneralConfig = GeneralConfig()

	@SerialEntry
	val uiAndVisuals: UIAndVisualsConfig = UIAndVisualsConfig()

	@SerialEntry
	val inventory: InventoryConfig = InventoryConfig()

	@SerialEntry
	val events: EventsConfig = EventsConfig()

	@SerialEntry
	val fishing: FishingConfig = FishingConfig()

	@SerialEntry
	val mining: MiningConfig = MiningConfig()

	@SerialEntry
	val dungeons: DungeonsConfig = DungeonsConfig()

	@SerialEntry
	val chat: ChatConfig = ChatConfig()

	@SerialEntry
	val qol: QOLConfig = QOLConfig()

	@SerialEntry
	val repo: RepoConfig = RepoConfig()
}