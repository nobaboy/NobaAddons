package me.nobaboy.nobaaddons.commands

import me.nobaboy.nobaaddons.commands.internal.Command
import me.nobaboy.nobaaddons.commands.internal.Group
import me.nobaboy.nobaaddons.repo.RepoManager
import me.nobaboy.nobaaddons.utils.TextUtils.buildLiteral
import me.nobaboy.nobaaddons.utils.TextUtils.openUrl
import me.nobaboy.nobaaddons.utils.TextUtils.underline
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import me.nobaboy.nobaaddons.utils.tr
import net.minecraft.util.Formatting

@Suppress("unused")
object RepoCommands : Group("repo") {
	val update = Command.async("update") {
		ChatUtils.addMessage("Updating repository...")
		RepoManager.update(true)
	}

	val info = Command("info") {
		if(!RepoManager.loaded) {
			val command = buildLiteral("/noba repo update") { formatted(Formatting.AQUA) }
			ChatUtils.addMessage(tr("nobaaddons.repo.notLoaded", "The repository hasn't been loaded yet! Try using $command to fix this."))
			ChatUtils.addMessage(tr("nobaaddons.repo.joinDiscord", "Please join the Discord for support if this persists."))
			return@Command
		}

		val commit = RepoManager.commit
		if(commit == "backup-repo") {
			ChatUtils.addMessage(tr("nobaaddons.repo.usingBackup", "Using backup repository; some features might not work!"), color = Formatting.YELLOW)
			ChatUtils.addMessage(tr("nobaaddons.repo.joinDiscord", "Please join the Discord for support if this persists."))
			return@Command
		}

		if(commit == null) {
			ChatUtils.addMessage(tr("nobaaddons.repo.unknownCommit", "I don't know what version I'm using(?!) (please report this in the Discord)"), color = Formatting.RED)
			return@Command
		}

		val url = RepoManager.commitUrl(commit)
		val commitText = buildLiteral(commit.take(8)) { openUrl(url).underline() }
		ChatUtils.addMessage(tr("nobaaddons.repo.currentCommit", "Current repo version: $commitText"))
	}
}