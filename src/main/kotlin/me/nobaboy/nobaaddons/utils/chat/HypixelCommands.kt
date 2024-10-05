package me.nobaboy.nobaaddons.utils.chat

object HypixelCommands {

    // Messaging Commands
    fun privateChat(player: String, message: String) {
        send("msg $player $message")
    }

    fun guildChat(message: String) {
        send("gc $message")
    }

    fun partyChat(message: String) {
        send("pc $message")
    }

    // Inventory Related Commands
    /**
     * Retrieves items from the sacks with the specified item ID obtained from wiki.hypixel.net
     *
     * @param itemID The internal ID that's obtained from the wiki
     */
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

    // Helper function to send commands through queue system.
    private fun send(command: String) {
        ChatUtils.queueCommand(command)
    }
}