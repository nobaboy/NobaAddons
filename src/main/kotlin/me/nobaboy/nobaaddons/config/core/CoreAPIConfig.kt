package me.nobaboy.nobaaddons.config.core

import dev.isxander.yacl3.api.ConfigCategory
import me.nobaboy.nobaaddons.config.option.stringController
import me.nobaboy.nobaaddons.config.option.tickBoxController
import me.nobaboy.nobaaddons.config.utils.label
import me.nobaboy.nobaaddons.utils.TextUtils.red
import me.nobaboy.nobaaddons.utils.TextUtils.toText
import me.nobaboy.nobaaddons.utils.TextUtils.underline
import me.nobaboy.nobaaddons.utils.TextUtils.yellow
import me.nobaboy.nobaaddons.utils.tr
import net.fabricmc.loader.api.FabricLoader

object CoreAPIConfig : AbstractCoreConfig("api") {
	@Order(0)
	var username by config("nobaboy") {
		name = tr("nobaaddons.config.apis.repo.username", "Repo Username")
		stringController()
	}

	@Order(1)
	var repository by config("NobaAddons-REPO") {
		name = tr("nobaaddons.config.apis.repo.repository", "Repo Repository")
		stringController()
	}

	@Order(2)
	var branch by config("main") {
		name = tr("nobaaddons.config.apis.repo.branch", "Repo Branch")
		stringController()
	}

	@Order(3)
	var autoUpdate by config(!FabricLoader.getInstance().isDevelopmentEnvironment) {
		name = tr("nobaaddons.config.apis.repo.autoUpdate", "Automatically Update Repo")
		tickBoxController()
	}

	override fun buildConfig(category: ConfigCategory.Builder) {
		category.label(
			tr("nobaaddons.config.apis.disclaimer.line1", "You should not be modifying these settings unless you've been explicitly instructed to after requesting support!").red().underline(),
			"".toText(),
			tr("nobaaddons.config.apis.disclaimer.line2", "Modifying these settings has the potential to break the mod!").yellow()
		)
		buildConfig(category, tr("nobaaddons.config.apis.repo", "Repo"))
	}
}