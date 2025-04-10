package me.nobaboy.nobaaddons.config.util

import dev.celestialfault.histoire.Exclude
import dev.celestialfault.histoire.Histoire
import dev.celestialfault.histoire.migrations.Migrations
import me.nobaboy.nobaaddons.NobaAddons
import java.util.UUID

/**
 * [dev.celestialfault.histoire.Histoire] subtype designed to be used for profile data
 *
 * @see ProfileDataLoader
 */
abstract class ProfileData protected constructor(@Exclude val profile: UUID?, fileName: String, migrations: Migrations? = null) : Histoire(
	path = ProfileDataLoader.PROFILES_DIR.resolve(profile.toString()).resolve(fileName),
	migrations = migrations,
	json = NobaAddons.JSON,
	atomicWrite = false,
	createBackup = false,
)