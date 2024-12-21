package me.nobaboy.nobaaddons.commands.debug

import me.nobaboy.nobaaddons.commands.internal.Command
import me.nobaboy.nobaaddons.commands.internal.Group
import me.nobaboy.nobaaddons.repo.Repo

@Suppress("unused")
object RepoDebugCommands : Group("repo") {
	val dumpKnownStrings = Command.command("dumpstrings") {
		executes {
			println("--- KNOWN STRING KEYS ---")
			Repo.knownStringKeys.forEach(::println)
			println("-------------------------")
		}
	}

	val dumpKnownRegex = Command.command("dumpregex") {
		executes {
			println("--- KNOWN REGEX KEYS ---")
			Repo.knownRegexKeys.forEach(::println)
			println("------------------------")
		}
	}

	val dumpObjects = Command.command("dumpobjects") {
		executes {
			println("--- KNOWN REPO OBJECTS ---")
			Repo.objects().forEach(::println)
			println("--------------------------")
		}
	}
}