package me.nobaboy.nobaaddons.commands.debug

import me.nobaboy.nobaaddons.commands.internal.Command
import me.nobaboy.nobaaddons.commands.internal.Group
import me.nobaboy.nobaaddons.repo.Repo

@Suppress("unused")
object RepoDebugCommands : Group("repo") {
	val dumpKnownStrings = Command("dumpstrings") {
		println("--- KNOWN STRING KEYS ---")
		Repo.knownStringKeys.forEach(::println)
		println("-------------------------")
	}

	val dumpKnownRegex = Command("dumpregex") {
		println("--- KNOWN REGEX KEYS ---")
		Repo.knownRegexKeys.forEach(::println)
		println("------------------------")
	}

	val dumpObjects = Command("dumpobjects") {
		println("--- KNOWN REPO OBJECTS ---")
		Repo.objects.forEach(::println)
		println("--------------------------")
	}
}