package me.nobaboy.nobaaddons

import com.mojang.logging.LogUtils
import kotlinx.coroutines.*
import me.nobaboy.nobaaddons.api.DungeonAPI
import me.nobaboy.nobaaddons.api.PartyAPI
import me.nobaboy.nobaaddons.api.SkyblockAPI
import me.nobaboy.nobaaddons.commands.NobaCommand
import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.features.chat.filter.dungeon.BlessingFilter
import me.nobaboy.nobaaddons.features.chat.filter.dungeon.HealerOrbFilter
import me.nobaboy.nobaaddons.features.chat.filter.dungeon.PickupObtainFilter
import me.nobaboy.nobaaddons.features.chatcommands.impl.DMCommands
import me.nobaboy.nobaaddons.features.chatcommands.impl.GuildCommands
import me.nobaboy.nobaaddons.features.chatcommands.impl.PartyCommands
import me.nobaboy.nobaaddons.utils.ModAPIUtils.listen
import me.nobaboy.nobaaddons.utils.ModAPIUtils.subscribeToEvent
import me.nobaboy.nobaaddons.utils.Scheduler
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.hypixel.modapi.HypixelModAPI
import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket
import net.minecraft.client.MinecraftClient
import net.minecraft.text.MutableText
import net.minecraft.text.Style
import net.minecraft.text.Text
import org.slf4j.Logger
import java.nio.file.Path

object NobaAddons : ClientModInitializer {
    const val MOD_ID = "nobaaddons"
    val VERSION = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow().metadata.version.friendlyString

    val PREFIX: MutableText
        get() = Text.empty()
            .append(Text.translatable("nobaaddons.name")
            .append(Text.literal(" > ")).setStyle(
                Style.EMPTY.withColor(0x007AFF).withBold(true))
            )

    val LOGGER: Logger = LogUtils.getLogger()
    val mc: MinecraftClient get() = MinecraftClient.getInstance()
    val modDir: Path get() = FabricLoader.getInstance().configDir

    private val supervisorJob = SupervisorJob()
    private val coroutineScope = CoroutineScope(CoroutineName(MOD_ID) + supervisorJob)

    fun runAsync(runnable: suspend CoroutineScope.() -> Unit) = coroutineScope.launch(block = runnable)

    override fun onInitializeClient() {
        NobaConfigManager.init()

        // Apis/Utils
        PartyAPI.init()
        DungeonAPI.init()
        Scheduler.schedule(20, repeat = true, ChatUtils::tickCommandQueue)

        // Commands
        NobaCommand.init()

        // Chat Commands
        DMCommands.init()
        PartyCommands.init()
        GuildCommands.init()

        // Chat Filters
        BlessingFilter.init()
        HealerOrbFilter.init()
        PickupObtainFilter.init()

        HypixelModAPI.getInstance().subscribeToEvent<ClientboundLocationPacket>()
        HypixelModAPI.getInstance().listen<ClientboundLocationPacket>(SkyblockAPI::onLocationPacket)
    }
}
