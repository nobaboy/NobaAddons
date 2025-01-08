package me.nobaboy.nobaaddons.config

import dev.celestialfault.celestialconfig.AbstractConfig
import dev.celestialfault.celestialconfig.migrations.Migrations
import dev.isxander.yacl3.api.YetAnotherConfigLib
import kotlinx.io.IOException
import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.config.categories.*
import me.nobaboy.nobaaddons.config.configs.*
import me.nobaboy.nobaaddons.utils.CommonText
import net.minecraft.client.gui.screen.Screen
import java.text.SimpleDateFormat
import java.util.Date

/*
 * Migrations MUST be added at the end of this block, otherwise they will NOT run. Executed migrations are
 * skipped, so new changes must be added as separate migrations. Removing pre-existing migrations is NOT supported
 * and will cause player configs to completely break, so avoid doing so.
 */
private val migrations = Migrations.create {
	add(migration = ::`001_removeYaclVersion`)
	add(migration = ::`002_inventoryCategory`)
	add(migration = ::`003_renameGlaciteMineshaftShareCorpses`)
}

private val CONFIG_PATH = NobaAddons.CONFIG_DIR.resolve("config.json")

class NobaConfig private constructor() : AbstractConfig(CONFIG_PATH, migrations = migrations) {
	val general by GeneralConfig()
	val uiAndVisuals by UIAndVisualsConfig()
	val inventory by InventoryConfig()
	val events by EventsConfig()
	val slayers by SlayersConfig()
	val fishing by FishingConfig()
	val mining by MiningConfig()
	val dungeons by DungeonsConfig()
	val chat by ChatConfig()
	val qol by QOLConfig()
	val repo by RepoConfig()

	companion object {
		@JvmField
		val INSTANCE = NobaConfig()

		fun init() {
			try {
				INSTANCE.load()
			} catch(ex: IOException) {
				val configPath = NobaAddons.CONFIG_DIR.resolve("config.json")

				val dateFormatter = SimpleDateFormat("yyyy-MM-dd")
				val date = dateFormatter.format(Date())

				val backupFileName = generateSequence(1) { it + 1 }
					.map { "config-$date-$it.json.bak" }
					.first { !configPath.resolveSibling(it).toFile().exists() }

				val backupFile = configPath.resolveSibling(backupFileName).toFile()

				NobaAddons.LOGGER.error("Failed to load config", ex)
				if(configPath.toFile().renameTo(backupFile)) {
					NobaAddons.LOGGER.error("Config file has been moved to $backupFile")
				}
			}
		}

		fun getConfigScreen(parent: Screen?): Screen {
			val defaults = NobaConfig()

			return YetAnotherConfigLib.createBuilder().apply {
				title(CommonText.NOBAADDONS)

				category(GeneralCategory.create(defaults, INSTANCE))
				category(UIAndVisualsCategory.create(defaults, INSTANCE))
				category(InventoryCategory.create(defaults, INSTANCE))
				category(EventsCategory.create(defaults, INSTANCE))
				category(SlayersCategory.create(defaults, INSTANCE))
				category(FishingCategory.create(defaults, INSTANCE))
				category(MiningCategory.create(defaults, INSTANCE))
				category(DungeonsCategory.create(defaults, INSTANCE))
				category(ChatCategory.create(defaults, INSTANCE))
				category(QOLCategory.create(defaults, INSTANCE))
				category(ApiCategory.create(defaults, INSTANCE))

				save(INSTANCE::save)
			}.build().generateScreen(parent)
		}
	}
}