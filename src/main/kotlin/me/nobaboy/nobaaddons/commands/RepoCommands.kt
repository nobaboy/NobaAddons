package me.nobaboy.nobaaddons.commands

import dev.celestialfault.commander.annotations.Command
import dev.celestialfault.commander.annotations.Group
import me.nobaboy.nobaaddons.repo.RepoManager
import me.nobaboy.nobaaddons.utils.mc.TextUtils.buildLiteral
import me.nobaboy.nobaaddons.utils.mc.TextUtils.openUrl
import me.nobaboy.nobaaddons.utils.mc.TextUtils.underline
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import me.nobaboy.nobaaddons.utils.tr
import net.minecraft.util.Formatting

@Suppress("unused")
@Group("repo")
object RepoCommands {
	@Command
	suspend fun update(force: Boolean = false) {
		ChatUtils.addMessage(tr("nobaaddons.repo.updateStarted", "Updating repository..."))
		RepoManager.update(true, force)
	}

	@Command
	fun info() {
		val commit = RepoManager.commit
		if(commit == "backup-repo") {
			ChatUtils.addMessage(tr("nobaaddons.repo.usingBackup", "Using backup repository; some features might not work!"), color = Formatting.YELLOW)
			ChatUtils.addMessage(tr("nobaaddons.repo.joinDiscord", "Please join the Discord for support if this persists."))
			return
		}

		if(commit == null) {
			ChatUtils.addMessage(tr("nobaaddons.repo.unknownCommit", "I don't know what repo version I'm using; the repo may not have been properly loaded! (please report this in the Discord)"), color = Formatting.RED)
			return
		}

		val url = RepoManager.commitUrl(commit)
		val commitText = buildLiteral(commit.take(8)) { openUrl(url).underline() }
		ChatUtils.addMessage(tr("nobaaddons.repo.currentCommit", "Current repo version: $commitText"))
	}
}