package me.nobaboy.nobaaddons.config

import dev.isxander.yacl3.config.v2.api.SerialEntry
import me.nobaboy.nobaaddons.config.configs.*

class NobaConfig {
    @SerialEntry
    val version: Int = NobaConfigManager.CONFIG_VERSION

    @SerialEntry
    val general: GeneralConfig = GeneralConfig()

//    @SerialEntry
//    val visuals: visualsConfig = VisualsConfig

    @SerialEntry
    val chat: ChatConfig = ChatConfig()

    @SerialEntry
    val chatCommands: ChatCommandsConfig = ChatCommandsConfig()
}