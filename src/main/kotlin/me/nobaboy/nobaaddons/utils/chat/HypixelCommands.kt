package me.nobaboy.nobaaddons.utils.chat

import me.nobaboy.nobaaddons.api.HypixelAPI
import me.nobaboy.nobaaddons.api.PartyAPI
import me.nobaboy.nobaaddons.utils.MCUtils
import net.fabricmc.loader.api.FabricLoader

object HypixelCommands {
	private val inParty: Boolean get() {
		if(FabricLoader.getInstance().isDevelopmentEnvironment && MCUtils.client.isInSingleplayer) {
			return true
		}
		return HypixelAPI.onHypixel && PartyAPI.party != null
	}

	// Messaging Commands
	fun allChat(message: String) {
		send("ac $message")
	}

	fun privateChat(player: String, message: String) {
		send("msg $player $message")
	}

	fun guildChat(message: String) {
		send("gc $message")
	}

	fun partyChat(message: String, partyCheck: Boolean = true) {
		if(partyCheck && !inParty) return
		send("pc $message")
	}

	// Inventory Related Commands
	fun getFromSacks(itemID: String, amount: Int) {
		send("gfs $itemID $amount")
	}

	// Party Related Commands
	fun partyInvite(player: String) {
		send("party invite $player")
	}

	fun partyJoin(player: String) {
		send("party join $player")
	}

	fun partyLeave() {
		send("party leave")
	}

	fun partyDisband() {
		send("party disband")
	}

	fun partyKick(player: String) {
		send("party kick $player")
	}

	fun partyTransfer(player: String) {
		send("party transfer $player")
	}

	fun partyAllInvite() {
		send("party settings allinvite")
	}

	fun partyWarp() {
		send("party warp")
	}

	fun partyList() {
		send("party list")
	}

	// Helper function to send commands through queue system
	private fun send(command: String) {
		ChatUtils.queueCommand(command)
	}
}