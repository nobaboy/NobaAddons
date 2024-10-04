package me.nobaboy.fabricmodtemplate

import com.mojang.logging.LogUtils
import kotlinx.coroutines.*
import net.fabricmc.api.ClientModInitializer
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import org.slf4j.Logger

object FabricModTemplate : ClientModInitializer {
    @JvmField val LOGGER: Logger = LogUtils.getLogger()
    val PREFIX: MutableText = Text.literal("")

    private val supervisorJob = SupervisorJob()
    private val coroutineScope = CoroutineScope(
        CoroutineName("FabricModTemplate") + supervisorJob
    )

    override fun onInitializeClient() {

    }
}