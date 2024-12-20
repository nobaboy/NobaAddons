package me.nobaboy.nobaaddons.commands

import me.nobaboy.nobaaddons.commands.internal.Command
import me.nobaboy.nobaaddons.commands.internal.Group
import me.nobaboy.nobaaddons.repo.Repo
import me.nobaboy.nobaaddons.utils.chat.ChatUtils

@Suppress("unused")
object RepoCommands : Group("repo") {
	val pull = Command.Companion.command("pull") {
		executes {
			ChatUtils.addMessage("Updating repository")
			Repo.announceRepoUpdate = true
			Repo.update()
		}
	}

	val commit = Command.Companion.command("commit") {
		executes {
			ChatUtils.addMessage("Current repository commit: ${Repo.commit.take(8)}")
		}
	}
}