package com.nobaboy.skyblockessentials.config;

import com.nobaboy.skyblockessentials.SkyblockEssentials;
import gg.essential.vigilance.Vigilant;
import gg.essential.vigilance.data.Property;
import gg.essential.vigilance.data.PropertyType;

import java.io.File;

@SuppressWarnings("unused")
public class SkyblockEssentialsConfig extends Vigilant {

    @Property(
        type = PropertyType.SWITCH,
        name = "Party Commands",
        description = "Allows everyone in party to use party commands.\nUsable commands: !help, !warp [optional: time], !cancel, !info",
        category = "Commands"
    )
    public static boolean partyCommands = true;

    @Property(
        type = PropertyType.SWITCH,
        name = "Guild Commands",
        description = "Allows everyone in guild to use guild commands.\nUsable commands: !help, !warpout",
        category = "Commands"
    )
    public static boolean guildCommands = true;

    @Property(
        type = PropertyType.SWITCH,
        name = "Cakes Eaten Notifier",
        description = "Notifies you once you've eaten all Century Cakes.",
        category = "Notifiers"
    )
    public static boolean cakesEatenNotifier = true;

    public static SkyblockEssentialsConfig INSTANCE = new SkyblockEssentialsConfig();

    public SkyblockEssentialsConfig() {
        super(new File("./config/skyblockessentials.toml"), "SkyblockEssentials (" + SkyblockEssentials.MOD_VERSION + ")");
        initialize();
    }
}
