package me.nobaboy.nobaaddons.features.chatcommands

interface IChatCommand {
    val name: String

    val usage: String
        get() = name

    fun run(ctx: ChatContext)

    val isEnabled: Boolean
        get() = true

    val aliases: MutableList<String>
        get() = mutableListOf()

    val bypassCooldown: Boolean
        get() = false
}