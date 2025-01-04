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
import java.util.Locale

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

class NobaConfig private constructor() : AbstractConfig(NobaAddons.CONFIG_DIR.resolve("config.json"), migrations = migrations) {
	val general by GeneralConfig()
	val uiAndVisuals by UIAndVisualsConfig()
	val inventory by InventoryConfig()
	val events by EventsConfig()
	val fishing by FishingConfig()
	val mining by MiningConfig()
	val dungeons by DungeonsConfig()
	val chat by ChatConfig()
	val qol by QOLConfig()
	val repo by RepoConfig()

	companion object {
		@JvmField
		val INSTANCE = NobaConfig()

		// This is very crude, I wanted to save a backup on client stop, on failure, load that backup, send a message
		// in game saying that the config got rolled back to whatever date due to failure
		fun init() {
			val configFilePath = NobaAddons.CONFIG_DIR.resolve("config.json")
			val backupFilePath = configFilePath.resolveSibling("config.json.bak")
			val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)

			try {
				val configFile = configFilePath.toFile()

				// so do I keep this
				configFile.takeIf { it.exists() }?.let {
					val backupFile = backupFilePath.toFile()

					backupFile.takeIf { it.exists() }?.delete()
					it.copyTo(backupFile, overwrite = true)
				}

				INSTANCE.load()
			} catch(e: IOException) {
				NobaAddons.LOGGER.error("Failed to load config", e)

				val configFile = configFilePath.toFile()
				if(configFile.exists()) {
					val date = dateFormat.format(Date())
					val newFileName = generateSequence(1) { it + 1 }
						.map { "config-$date-$it.json" }
						.first { !configFilePath.resolveSibling(it).toFile().exists() }

					configFile.renameTo(configFilePath.resolveSibling(newFileName).toFile())
					NobaAddons.LOGGER.info("Renamed config file to $newFileName due to failure")
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