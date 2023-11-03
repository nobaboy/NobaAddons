package com.nobaboy.nobaaddons.util;

import com.nobaboy.nobaaddons.NobaAddons;
import net.minecraft.client.Minecraft;
import net.minecraft.scoreboard.ScoreObjective;

import java.util.List;

public class Utils {
    private static final Minecraft mc = Minecraft.getMinecraft();
    public static boolean inSkyblock;
    public static boolean inDungeons;

    public static boolean isOnHypixel() {
        if(NobaAddons.config.debugMode) return true;
        try {
            if(mc != null && mc.theWorld != null && !mc.isSingleplayer()) {
                if(mc.thePlayer != null && mc.thePlayer.getClientBrand() != null) {
                    if(mc.thePlayer.getClientBrand().toLowerCase().contains("hypixel")) return true;
                }
                if(mc.getCurrentServerData() != null) return mc.getCurrentServerData().serverIP.toLowerCase().contains("hypixel");
            }
            return false;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Taken from Danker's Skyblock Mod under GPL 3.0 license
     * https://github.com/bowser0000/SkyblockMod/blob/master/LICENSE
     * @author bowser0000
     */
    public static void checkForSkyblock() {
        if(NobaAddons.config.debugMode) { inSkyblock = true; return; }
        if(isOnHypixel()) {
            ScoreObjective scoreboardObj = mc.theWorld.getScoreboard().getObjectiveInDisplaySlot(1);
            if(scoreboardObj != null) {
                String scObjName = ScoreboardUtil.cleanSB(scoreboardObj.getDisplayName());
                if(scObjName.contains("SKYBLOCK") || scObjName.contains("SKIBLOCK")) {
                    inSkyblock = true;
                    return;
                }
            }
        }
        inSkyblock = false;
    }

    /**
     * Taken from Danker's Skyblock Mod under GPL 3.0 license
     * https://github.com/bowser0000/SkyblockMod/blob/master/LICENSE
     * @author bowser0000
     */
    public static void checkForDungeons() {
        if(NobaAddons.config.debugMode) { inDungeons = true; return; }
        if(inSkyblock) {
            List<String> scoreboard = ScoreboardUtil.getSidebarLines();
            for(String s : scoreboard) {
                String sCleaned = ScoreboardUtil.cleanSB(s);
                if((sCleaned.contains("The Catacombs") && !sCleaned.contains("Queue")) || sCleaned.contains("Dungeon Cleared:")) {
                    inDungeons = true;
                    return;
                }
            }
        }
        inDungeons = false;
    }
}
