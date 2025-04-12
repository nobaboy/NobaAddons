package me.nobaboy.nobaaddons.commands.debug

import dev.celestialfault.commander.annotations.Command
import dev.celestialfault.commander.annotations.Group
import me.nobaboy.nobaaddons.repo.Repo

@Suppress("unused")
@Group("repo")
object RepoDebugCommands {
	@Command
	fun dumpStrings() {
		println("--- KNOWN STRING KEYS ---")
		Repo.usedStringKeys.forEach(::println)
		println("-------------------------")
	}

	@Command
	fun dumpRegex() {
		println("--- KNOWN REGEX KEYS ---")
		Repo.usedRegexKeys.forEach(::println)
		println("------------------------")
	}

	@Command
	fun dumpObjects() {
		println("--- KNOWN REPO OBJECTS ---")
		Repo.objects.forEach(::println)
		println("--------------------------")
	}

	@Command
	fun logMismatchedValues() {
		Repo.logMismatchedHandles()
	}
}