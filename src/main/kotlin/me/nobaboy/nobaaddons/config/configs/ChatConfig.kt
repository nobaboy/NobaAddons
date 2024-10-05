package me.nobaboy.nobaaddons.config.configs

import dev.isxander.yacl3.config.v2.api.SerialEntry
import me.nobaboy.nobaaddons.config.impl.ChatFilterOption

class ChatConfig {
    @SerialEntry
    val filter: Filter = Filter()

    class Filter {
        @SerialEntry
        var tipMessage: Boolean = false

        @SerialEntry
        var blessingMessage: ChatFilterOption = ChatFilterOption.SHOWN

        @SerialEntry
        var healerOrbMessage: ChatFilterOption = ChatFilterOption.SHOWN

        @SerialEntry
        var pickupObtainMessage: Boolean = false

        @SerialEntry
        var allow5050ItemMessage: Boolean = false
    }
}