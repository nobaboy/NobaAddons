package me.nobaboy.nobaaddons.config

import dev.celestialfault.celestialconfig.AbstractConfig
import dev.celestialfault.celestialconfig.migrations.Migrations
import dev.isxander.yacl3.api.YetAnotherConfigLib
import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.config.categories.*
import me.nobaboy.nobaaddons.config.configs.*
import me.nobaboy.nobaaddons.utils.CommonText
import net.minecraft.client.gui.screen.Screen

/*
 * Migrations MUST be added at the end of this block, otherwise they will NOT run. Migrations that have already been
 * applied are skipped, so new changes must be added as separate migrations. Removing pre-existing migrations is
 * NOT supported and will cause player configs to completely break, so avoid doing so.
 */
private val migrations = Migrations.create {
	add(migration = ::`001_removeYaclVersion`)
	add(migration = ::`002_inventoryCategory`)
	add(migration = ::`003_renameGlaciteMineshaftShareCorpses`)
	add(migration = ::`004_moveHideOtherPeopleFishing`)
}

private val CONFIG_PATH = NobaAddons.CONFIG_DIR.resolve("config.json")

/*
 * If you are adding a new category and config, please add it in alphabetically in its designated group.
 */
class NobaConfig private constructor() : AbstractConfig(CONFIG_PATH, migrations = migrations) {
	val general by GeneralConfig()
	val uiAndVisuals by UIAndVisualsConfig()
	val inventory by InventoryConfig()
	val events by EventsConfig()
	val slayers by SlayersConfig()
	// region Skills
	val fishing by FishingConfig()
	val mining by MiningConfig()
	// endregion
	// region Islands
	val dungeons by DungeonsConfig()
	val rift by RiftConfig()
	// endregion
	val chat by ChatConfig()
	val qol by QOLConfig()
	val repo by RepoConfig()

	companion object {
		@JvmField
		val INSTANCE = NobaConfig()

		/**
		 * Returns a newly created instance of [NobaConfig] to allow for accessing default values
		 */
		val DEFAULTS get() = NobaConfig()

		fun getConfigScreen(parent: Screen?): Screen = YetAnotherConfigLib.createBuilder().apply {
			title(CommonText.NOBAADDONS)

			category(GeneralCategory.create())
			category(UIAndVisualsCategory.create())
			category(InventoryCategory.create())
			category(EventsCategory.create())
			category(SlayersCategory.create())
			category(FishingCategory.create())
			category(MiningCategory.create())
			category(DungeonsCategory.create())
			category(RiftCategory.create())
			category(ChatCategory.create())
			category(QOLCategory.create())
			category(ApiCategory.create())

			save(INSTANCE::save)
		}.build().generateScreen(parent)
	}
}