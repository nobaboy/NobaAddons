package me.nobaboy.nobaaddons

import com.mojang.logging.LogUtils
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.config.util.safeLoad
import me.nobaboy.nobaaddons.generated.NobaAddonsApis
import me.nobaboy.nobaaddons.generated.NobaAddonsConfigs
import me.nobaboy.nobaaddons.generated.NobaAddonsCoreModules
import me.nobaboy.nobaaddons.generated.NobaAddonsModules
import me.nobaboy.nobaaddons.utils.CommonText
import me.nobaboy.nobaaddons.utils.ErrorManager
import me.nobaboy.nobaaddons.utils.mc.TextUtils.blue
import me.nobaboy.nobaaddons.utils.mc.TextUtils.bold
import me.nobaboy.nobaaddons.utils.mc.TextUtils.buildText
import me.nobaboy.nobaaddons.utils.mc.TextUtils.darkGray
import me.nobaboy.nobaaddons.utils.mc.TextUtils.literal
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.Version
import net.minecraft.text.Text
import org.slf4j.Logger
import java.nio.file.Path

object NobaAddons : ClientModInitializer {
	const val MOD_ID = "nobaaddons"

	val VERSION_INFO: Version = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow().metadata.version
	val VERSION: String = VERSION_INFO.friendlyString

	val PREFIX: Text get() = buildText {
		append(if(NobaConfig.general.compactModMessagePrefix) CommonText.NOBA else CommonText.NOBAADDONS)
		literal(" » ") { darkGray() }
		blue().bold()
	}

	val LOGGER: Logger = LogUtils.getLogger()
	val CONFIG_DIR: Path get() = FabricLoader.getInstance().configDir.resolve(MOD_ID)

	val JSON = Json {
		ignoreUnknownKeys = true
		encodeDefaults = true
		prettyPrint = true
	}

	private val supervisorJob = SupervisorJob()
	private val exceptionHandler = CoroutineExceptionHandler { _, error ->
		ErrorManager.logError("Encountered an unhandled error in an async context", error)
	}
	val coroutineScope = CoroutineScope(CoroutineName(MOD_ID) + supervisorJob + exceptionHandler)

	fun runAsync(runnable: suspend CoroutineScope.() -> Unit) = coroutineScope.launch(block = runnable)

	override fun onInitializeClient() {
		NobaAddonsConfigs.collected.forEach { it.safeLoad() }
		NobaAddonsCoreModules
		NobaAddonsApis
		NobaAddonsModules
	}
}