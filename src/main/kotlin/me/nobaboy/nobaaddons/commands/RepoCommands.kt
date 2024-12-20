package me.nobaboy.nobaaddons.commands

import me.nobaboy.nobaaddons.commands.internal.Command
import me.nobaboy.nobaaddons.commands.internal.Group
import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.repo.Repo
import me.nobaboy.nobaaddons.utils.TextUtils.openUrl
import me.nobaboy.nobaaddons.utils.TextUtils.toText
import me.nobaboy.nobaaddons.utils.TextUtils.translatable
import me.nobaboy.nobaaddons.utils.TextUtils.underline
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import net.minecraft.util.Formatting

@Suppress("unused")
object RepoCommands : Group("repo") {
	val update = Command.Companion.command("update") {
		executes {
			ChatUtils.addMessage { translatable("nobaaddons.repo.updateStarted") }
			Repo.announceRepoUpdate = true
			Repo.update()
		}
	}

	val info = Command.Companion.command("info") {
		executes {
			if(!Repo.loaded) {
				ChatUtils.addMessage(color = Formatting.RED) { translatable("nobaaddons.repo.version.notLoaded") }
				return@executes
			}

			val commit = Repo.commit
			if(commit == null) {
				ChatUtils.addMessage(color = Formatting.RED) { translatable("nobaaddons.repo.version.unknown") }
				return@executes
			}

			ChatUtils.addMessage {
				val url = NobaConfigManager.config.repo.uri.removeSuffix(".git") + "/commit/${Repo.commit}"
				translatable("nobaaddons.repo.version", commit.take(8).toText().openUrl(url).underline())
				if(Repo.isDirty) {
					append(" ")
					translatable("nobaaddons.repo.version.hasChanges")
				}
			}
		}
	}
}