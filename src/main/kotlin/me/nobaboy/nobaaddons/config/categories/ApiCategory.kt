package me.nobaboy.nobaaddons.config.categories

import dev.isxander.yacl3.api.Binding
import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.config.util.*
import me.nobaboy.nobaaddons.utils.TextUtils.red
import me.nobaboy.nobaaddons.utils.TextUtils.toText
import me.nobaboy.nobaaddons.utils.TextUtils.underline
import me.nobaboy.nobaaddons.utils.TextUtils.yellow
import me.nobaboy.nobaaddons.utils.tr
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.text.Text

private val OPERATORS = mapOf<String, (Int, Int) -> Int>("+" to Int::plus, "*" to Int::times, "-" to Int::minus)

private fun createSimpleMathProblem(): Pair<String, Int> {
	val a = (7..50).random()
	val b = (5..30).random()
	val operator = OPERATORS.toList().random()
	val result = operator.second.invoke(a, b)
	if(result == 0) {
		return createSimpleMathProblem()
	}
	return "$a ${operator.first} $b" to result
}

object ApiCategory {
	private val captcha: Option<Boolean> = Option.createBuilder<Boolean>().apply {
		name(Text.literal("internal captcha option"))
		binding(false, NobaConfig.INSTANCE.repo::solvedCaptcha, NobaConfig.INSTANCE.repo::solvedCaptcha.setter)
		controller(BooleanControllerBuilder::create)
	}.build()

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

		captcha()
		repo()
	}

	private fun ConfigCategory.Builder.captcha() {
		if(NobaConfig.INSTANCE.repo.solvedCaptcha) {
			return
		}

		var solved = 0
		val toSolve = createSimpleMathProblem()
		add(Binding.generic(0, { solved }, { solved = it })) {
			name = tr("nobaaddons.config.apis.captcha", "Solve: ${toSolve.first} =")
			intFieldController()
			onUpdate { option, event ->
				captcha.requestSet(option.pendingValue() == toSolve.second)
				captcha.applyValue()
			}
		}
	}

	private fun ConfigCategory.Builder.repo() {
		group(tr("nobaaddons.config.apis.repo", "Repo")) {
			add({ repo::username }) {
				name = tr("nobaaddons.config.apis.repo.username", "Repo Username")
				require { option(captcha) }
				stringController()
			}
			add({ repo::repository }) {
				name = tr("nobaaddons.config.apis.repo.repository", "Repo Repository")
				require { option(captcha) }
				stringController()
			}
			add({ repo::branch }) {
				name = tr("nobaaddons.config.apis.repo.branch", "Repo Branch")
				require { option(captcha) }
				stringController()
			}

			add({ repo::autoUpdate }) {
				name = tr("nobaaddons.config.apis.repo.autoUpdate", "Automatically Update Repo")
				if(FabricLoader.getInstance().isDevelopmentEnvironment) {
					descriptionText = "This setting is automatically disabled as you're in a development environment".toText()
				}
				require { option(captcha) }
				tickBoxController()
			}
		}
	}
}