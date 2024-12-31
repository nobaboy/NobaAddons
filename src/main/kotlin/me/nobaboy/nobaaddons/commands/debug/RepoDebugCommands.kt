package me.nobaboy.nobaaddons.commands.debug

import me.nobaboy.nobaaddons.commands.annotations.Command
import me.nobaboy.nobaaddons.commands.impl.Context
import me.nobaboy.nobaaddons.commands.annotations.Group
import me.nobaboy.nobaaddons.repo.Repo

@Suppress("unused")
@Group("repo")
object RepoDebugCommands {
	@Command
	fun dumpStrings(ctx: Context) {
		println("--- KNOWN STRING KEYS ---")
		Repo.knownStringKeys.forEach(::println)
		println("-------------------------")
	}

	@Command
	fun dumpRegex(ctx: Context) {
		println("--- KNOWN REGEX KEYS ---")
		Repo.knownRegexKeys.forEach(::println)
		println("------------------------")
	}

	@Command
	fun dumpObjects(ctx: Context) {
		println("--- KNOWN REPO OBJECTS ---")
		Repo.objects.forEach(::println)
		println("--------------------------")
	}
}