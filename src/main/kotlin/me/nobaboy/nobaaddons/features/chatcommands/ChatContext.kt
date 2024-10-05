package me.nobaboy.nobaaddons.features.chatcommands

class ChatContext(
    private val user: String,
    private val command: String,
    private val args: List<String>,
    private val fullMessage: String
) {
    fun user(): String = user
    fun command(): String = command
    fun args(): List<String> = args
    fun fullMessage(): String = fullMessage
}