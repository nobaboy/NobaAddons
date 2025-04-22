package me.nobaboy.nobaaddons.config

import dev.celestialfault.histoire.Histoire
import dev.celestialfault.histoire.Object
import dev.isxander.yacl3.api.YetAnotherConfigLib
import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.config.categories.*
import me.nobaboy.nobaaddons.config.configs.*
import me.nobaboy.nobaaddons.config.util.OptionBuilder
import me.nobaboy.nobaaddons.utils.CommonText
import net.minecraft.client.gui.screen.Screen

/*
 * If you are adding a new category and config, please add it in alphabetically in its designated group.
 */
sealed class AbstractNobaConfig protected constructor() : Histoire(
	NobaAddons.CONFIG_DIR.resolve("config.json").toFile(),
	migrations = migrations,
	json = NobaAddons.JSON,
	createBackup = true,
) {
	@Object val general = GeneralConfig()
	@Object val uiAndVisuals = UIAndVisualsConfig()
	@Object val inventory = InventoryConfig()
	@Object val events = EventsConfig()
	@Object val slayers = SlayersConfig()
	// region Skills
	@Object val fishing = FishingConfig()
	@Object val mining = MiningConfig()
	// endregion
	// region Islands
	@Object val crimsonIsle = CrimsonIsleConfig()
	@Object val dungeons = DungeonsConfig()
	@Object val rift = RiftConfig()
	// endregion
	@Object val chat = ChatConfig()
	@Object val qol = QOLConfig()
	@Object val repo = RepoConfig()

	// this must be present for use by migrations; this property is never directly referenced,
	// but it is read by migrations to determine if the loaded JsonObject needs to be modified.
	// histoire requires that this is a var, but it doesn't care about the visibility of it.
	@Suppress("unused")
	private var configVersion: Int = migrations.currentVersion
}

object NobaConfig : AbstractNobaConfig() {
	fun getConfigScreen(parent: Screen?): Screen = YetAnotherConfigLib.createBuilder().apply {
		title(CommonText.NOBAADDONS)
		OptionBuilder.defaults = NobaConfigDefaults()

		category(GeneralCategory.create())
		category(UIAndVisualsCategory.create())
		category(InventoryCategory.create())
		category(EventsCategory.create())
		category(SlayersCategory.create())
		category(FishingCategory.create())
		category(MiningCategory.create())
		category(CrimsonIsleCategory.create())
		category(DungeonsCategory.create())
		category(RiftCategory.create())
		category(ChatCategory.create())
		category(QOLCategory.create())
		category(ApiCategory.create())

		save(::save)
	}.build().generateScreen(parent)
}

private class NobaConfigDefaults : AbstractNobaConfig() {
	override fun save() {
		throw UnsupportedOperationException()
	}

	override fun load() {
		throw UnsupportedOperationException()
	}
}