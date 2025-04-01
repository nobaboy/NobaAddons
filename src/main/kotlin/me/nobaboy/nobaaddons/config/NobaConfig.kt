package me.nobaboy.nobaaddons.config

import dev.celestialfault.histoire.Histoire
import dev.celestialfault.histoire.Object
import dev.celestialfault.histoire.migrations.Migration
import dev.celestialfault.histoire.migrations.Migrations
import dev.isxander.yacl3.api.YetAnotherConfigLib
import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.config.categories.*
import me.nobaboy.nobaaddons.config.configs.*
import me.nobaboy.nobaaddons.config.util.OptionBuilder
import me.nobaboy.nobaaddons.utils.CommonText
import net.minecraft.client.gui.screen.Screen

/*
 * Migrations MUST be added at the end of this block, otherwise they will NOT run. Migrations that have already been
 * applied are skipped, so new changes must be added as separate migrations. Removing pre-existing migrations is
 * NOT supported and will cause player configs to completely break, so avoid doing so.
 */
private val migrations = Migrations("configVersion", buildList<Migration> {
	add(::`001_removeYaclVersion`)
	add(::`002_inventoryCategory`)
	add(::`003_renameGlaciteMineshaftShareCorpses`)
	add(::`004_moveHideOtherPeopleFishing`)
	add(::`005_renameEtherwarpHelper`)
})

private val CONFIG_PATH = NobaAddons.CONFIG_DIR.resolve("config.json")

/*
 * If you are adding a new category and config, please add it in alphabetically in its designated group.
 */
class NobaConfig private constructor() : Histoire(CONFIG_PATH.toFile(), migrations = migrations) {
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
	@Object val dungeons = DungeonsConfig()
	@Object val rift = RiftConfig()
	// endregion
	@Object val chat = ChatConfig()
	@Object val qol = QOLConfig()
	@Object val repo = RepoConfig()

	@Suppress("unused")
	var configVersion: Int = migrations!!.currentVersion

	companion object {
		@JvmField
		val INSTANCE = NobaConfig()

		fun getConfigScreen(parent: Screen?): Screen = YetAnotherConfigLib.createBuilder().apply {
			title(CommonText.NOBAADDONS)
			OptionBuilder.defaults = NobaConfig()

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