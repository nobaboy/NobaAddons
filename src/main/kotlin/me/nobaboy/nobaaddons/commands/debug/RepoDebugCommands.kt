package me.nobaboy.nobaaddons.commands.debug

import me.nobaboy.nobaaddons.commands.internal.ExecutableCommand
import me.nobaboy.nobaaddons.commands.internal.CommandGroup
import me.nobaboy.nobaaddons.repo.Repo

@Suppress("unused")
object RepoDebugCommands : CommandGroup("repo") {
	val dumpKnownStrings = ExecutableCommand("dumpstrings") {
		println("--- KNOWN STRING KEYS ---")
		Repo.knownStringKeys.forEach(::println)
		println("-------------------------")
	}

	val dumpKnownRegex = ExecutableCommand("dumpregex") {
		println("--- KNOWN REGEX KEYS ---")
		Repo.knownRegexKeys.forEach(::println)
		println("------------------------")
	}

	val dumpObjects = ExecutableCommand("dumpobjects") {
		println("--- KNOWN REPO OBJECTS ---")
		Repo.objects.forEach(::println)
		println("--------------------------")
	}
}