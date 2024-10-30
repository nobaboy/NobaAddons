package me.nobaboy.nobaaddons.features.chat.chatcommands

class ChatContext(
	val user: String,
	val command: String,
	val args: List<String>,
	val fullMessage: String
)