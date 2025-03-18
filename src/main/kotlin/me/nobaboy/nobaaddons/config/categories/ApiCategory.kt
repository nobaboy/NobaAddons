package me.nobaboy.nobaaddons.config.categories

import me.nobaboy.nobaaddons.config.util.add
import me.nobaboy.nobaaddons.config.util.group
import me.nobaboy.nobaaddons.config.util.category
import me.nobaboy.nobaaddons.config.util.descriptionText
import me.nobaboy.nobaaddons.config.util.label
import me.nobaboy.nobaaddons.config.util.stringController
import me.nobaboy.nobaaddons.config.util.tickBoxController
import me.nobaboy.nobaaddons.utils.TextUtils.red
import me.nobaboy.nobaaddons.utils.TextUtils.toText
import me.nobaboy.nobaaddons.utils.TextUtils.underline
import me.nobaboy.nobaaddons.utils.TextUtils.yellow
import me.nobaboy.nobaaddons.utils.tr
import net.fabricmc.loader.api.FabricLoader

object ApiCategory {
	fun create() = category(tr("nobaaddons.config.apis", "APIs")) {
		label {
			+tr(
				"nobaaddons.config.apis.disclaimer.line1",
				"You should not be modifying these settings unless you've been explicitly instructed to after requesting support!"
			).red().underline()

			newLine()

			+tr("nobaaddons.config.apis.disclaimer.line2", "Modifying these settings has the potential to break the mod!").yellow()
		}

		group(tr("nobaaddons.config.apis.repo", "Repo")) {
			add({ repo::username }) {
				name = tr("nobaaddons.config.apis.repo.username", "Repo Username")
				stringController()
			}
			add({ repo::repository }) {
				name = tr("nobaaddons.config.apis.repo.repository", "Repo Repository")
				stringController()
			}
			add({ repo::branch }) {
				name = tr("nobaaddons.config.apis.repo.branch", "Repo Branch")
				stringController()
			}

			add({ repo::autoUpdate }) {
				name = tr("nobaaddons.config.apis.repo.autoUpdate", "Automatically Update Repo")
				if(FabricLoader.getInstance().isDevelopmentEnvironment) {
					descriptionText = "This setting is automatically disabled as you're in a development environment".toText()
				}
				tickBoxController()
			}
		}
	}
}