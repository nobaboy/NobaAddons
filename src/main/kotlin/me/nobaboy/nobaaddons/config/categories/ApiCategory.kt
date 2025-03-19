package me.nobaboy.nobaaddons.config.categories

import dev.isxander.yacl3.api.ConfigCategory
import me.nobaboy.nobaaddons.config.util.*
import me.nobaboy.nobaaddons.utils.TextUtils.red
import me.nobaboy.nobaaddons.utils.TextUtils.toText
import me.nobaboy.nobaaddons.utils.TextUtils.underline
import me.nobaboy.nobaaddons.utils.TextUtils.yellow
import me.nobaboy.nobaaddons.utils.tr
import net.fabricmc.loader.api.FabricLoader

object ApiCategory {
	fun create() = category(tr("nobaaddons.config.apis", "APIs")) {
		label {
			append(
				tr(
					"nobaaddons.config.apis.disclaimer.line1",
					"You should not be modifying these settings unless you've been explicitly instructed to after requesting support!"
				).red().underline()
			)
			append("\n\n")
			append(tr("nobaaddons.config.apis.disclaimer.line2", "Modifying these settings has the potential to break the mod!").yellow())
		}

		repo()
	}

	private fun ConfigCategory.Builder.repo() {
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