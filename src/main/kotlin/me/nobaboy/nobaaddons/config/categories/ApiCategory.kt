package me.nobaboy.nobaaddons.config.categories

import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.config.NobaConfigUtils
import me.nobaboy.nobaaddons.config.NobaConfigUtils.buildGroup
import me.nobaboy.nobaaddons.config.NobaConfigUtils.label
import me.nobaboy.nobaaddons.config.NobaConfigUtils.string
import me.nobaboy.nobaaddons.config.NobaConfigUtils.tickBox
import me.nobaboy.nobaaddons.utils.TextUtils.red
import me.nobaboy.nobaaddons.utils.TextUtils.toText
import me.nobaboy.nobaaddons.utils.TextUtils.underline
import me.nobaboy.nobaaddons.utils.TextUtils.yellow
import me.nobaboy.nobaaddons.utils.tr
import net.fabricmc.loader.api.FabricLoader

object ApiCategory {
	fun create(defaults: NobaConfig, config: NobaConfig) = NobaConfigUtils.buildCategory(tr("nobaaddons.config.apis", "APIs")) {
		label(
			tr("nobaaddons.config.apis.disclaimer.line1", "You should not be modifying these settings unless you've been explicitly instructed to after requesting support!").red().underline(),
			"".toText(),
			tr("nobaaddons.config.apis.disclaimer.line2", "Modifying these settings has the potential to break the mod!").yellow()
		)

		buildGroup(tr("nobaaddons.config.apis.repo", "Repo")) {
			string(
				tr("nobaaddons.config.apis.repo.username", "Repo Username"),
				default = defaults.repo.username,
				property = config.repo::username
			)
			string(
				tr("nobaaddons.config.apis.repo.repository", "Repo Repository"),
				default = defaults.repo.repository,
				property = config.repo::repository
			)
			string(
				tr("nobaaddons.config.apis.repo.branch", "Repo Branch"),
				default = defaults.repo.branch,
				property = config.repo::branch
			)

			tickBox(
				tr("nobaaddons.config.apis.repo.autoUpdate", "Automatically Update Repo"),
				description = if(FabricLoader.getInstance().isDevelopmentEnvironment) "This setting is automatically disabled as you're in a development environment".toText() else null,
				default = defaults.repo.autoUpdate,
				property = config.repo::autoUpdate
			)
		}
	}
}