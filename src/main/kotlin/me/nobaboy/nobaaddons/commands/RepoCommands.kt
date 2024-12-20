package me.nobaboy.nobaaddons.commands

import me.nobaboy.nobaaddons.commands.internal.Command
import me.nobaboy.nobaaddons.commands.internal.Group
import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.repo.Repo
import me.nobaboy.nobaaddons.utils.TextUtils.buildText
import me.nobaboy.nobaaddons.utils.TextUtils.toText
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import net.minecraft.text.ClickEvent

@Suppress("unused")
object RepoCommands : Group("repo") {
	val update = Command.Companion.command("update") {
		executes {
			ChatUtils.addMessage("Updating repository")
			Repo.announceRepoUpdate = true
			Repo.update()
		}
	}

	val info = Command.Companion.command("info") {
		executes {
			if(!Repo.loaded) {
				ChatUtils.addMessage("The repository hasn't been loaded yet!")
				return@executes
			}

			ChatUtils.addMessage(buildText {
				append("Current repository version: ")
				val url = NobaConfigManager.config.repo.uri.removeSuffix(".git") + "/commit/${Repo.commit}"
				append(Repo.commit.take(8).toText().styled { it.withClickEvent(ClickEvent(ClickEvent.Action.OPEN_URL, url)).withUnderline(true) })
			})
		}
	}
}