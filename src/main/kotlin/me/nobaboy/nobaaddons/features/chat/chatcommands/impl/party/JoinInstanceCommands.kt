package me.nobaboy.nobaaddons.features.chat.chatcommands.impl.party

import me.nobaboy.nobaaddons.features.chat.chatcommands.ChatContext
import me.nobaboy.nobaaddons.features.chat.chatcommands.ChatCommand
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import me.nobaboy.nobaaddons.utils.tr
import kotlin.time.Duration.Companion.seconds

class JoinInstanceCommands : ChatCommand(2.seconds) {
	override val enabled: Boolean = config.party.joinInstanced

	override val usage: String = "f(1-7), m(1-7), t(1-5)"

	override val name: String = "f1"
	override val aliases: List<String> = listOf(
		/*f1,*/ "f2", "f3", "f4", "f5", "f6", "f7", // Catacombs
		"m1",   "m2", "m3", "m4", "m5", "m6", "m7", // Master Mode Catacombs
		"t1", "t2", "t3", "t4", "t5",               // Kuudra
	)

	override fun run(ctx: ChatContext) {
		val type = ctx.command.first().lowercase()
		val tier = ctx.command.last().toString().toInt()

		val instanceName = when(type) {
			"f", "m" -> "${if(type == "m") "master_" else ""}catacombs_floor_${floors[tier]}"
			"t" -> "kuudra_${kuudraTiers[tier]}"
			else -> throw MatchException(null, null)
		}
		ChatUtils.addMessage(tr("nobaaddons.chat.partyCommands.joiningInstance", "Joining ${type.uppercase()}$tier"))
		ChatUtils.queueCommand("joininstance $instanceName")
		startCooldown()
	}
}

private val floors = mapOf(
	1 to "one", 2 to "two", 3 to "three", 4 to "four", 5 to "five", 6 to "six", 7 to "seven"
)

private val kuudraTiers = mapOf(
	1 to "normal", 2 to "hot", 3 to "burning", 4 to "fiery", 5 to "infernal"
)