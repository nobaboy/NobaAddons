package me.nobaboy.nobaaddons.config.impl

import dev.isxander.yacl3.config.v2.api.SerialEntry

object ChatCommandsConfig {
    @SerialEntry
    val dm: DMCommandsConfig = DMCommandsConfig()

    @SerialEntry
    val party: PartyCommandsConfig = PartyCommandsConfig()

    @SerialEntry
    val guild: GuildCommandsConfig = GuildCommandsConfig()

    class DMCommandsConfig {
        @SerialEntry
        var enabled: Boolean = false

        @SerialEntry
        var help: Boolean = false

        @SerialEntry
        var warpMe: Boolean = false

        @SerialEntry
        var partyMe: Boolean = false

        @SerialEntry
        var warpOut: Boolean = false
    }

    class PartyCommandsConfig {
        @SerialEntry
        var enabled: Boolean = false

        @SerialEntry
        var help: Boolean = false

        @SerialEntry
        var allInvite: Boolean = false

        @SerialEntry
        var transfer: Boolean = false

        @SerialEntry
        var warp: Boolean = false

        @SerialEntry
        var coords: Boolean = false
    }

    class GuildCommandsConfig {
        @SerialEntry
        var enabled: Boolean = false

        @SerialEntry
        var help: Boolean = false

        @SerialEntry
        var warpOut: Boolean = false
    }
}