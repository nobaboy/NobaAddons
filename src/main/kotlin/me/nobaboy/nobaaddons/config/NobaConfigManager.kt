package me.nobaboy.nobaaddons.config

import com.google.gson.FieldNamingPolicy
import dev.isxander.yacl3.api.YetAnotherConfigLib
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder
import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.config.categories.ChatCategory
import me.nobaboy.nobaaddons.config.categories.ChatCommandsCategory
import me.nobaboy.nobaaddons.config.categories.GeneralCategory
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import java.lang.StackWalker.Option
import java.nio.file.Path

object NobaConfigManager {
	const val CONFIG_VERSION = 1
	private val CONFIG_DIR: Path = FabricLoader.getInstance().configDir.resolve("${NobaAddons.MOD_ID}/config.json")

	private val HANDLER: ConfigClassHandler<NobaConfig> = ConfigClassHandler.createBuilder(NobaConfig::class.java)
		.serializer { config ->
			GsonConfigSerializerBuilder.create(config)
				.setPath(CONFIG_DIR)
				.setJson5(false)
				.appendGsonBuilder { builder ->
					builder
						.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
						.registerTypeHierarchyAdapter(Identifier::class.java, Identifier.Serializer())
				}
				.build()
		}
		.build()

	fun get() = HANDLER.instance()

	fun init() {
		if(StackWalker.getInstance(Option.RETAIN_CLASS_REFERENCE).callerClass != NobaAddons::class.java) {
			throw RuntimeException("NobaAddons: Config initialized from an illegal place!")
		}

		HANDLER.load()
	}

	fun save() {
		HANDLER.save()
	}

	fun getConfigScreen(parent: Screen?): Screen {
		return YetAnotherConfigLib.create(HANDLER) { defaults, config, builder ->
			builder.title(Text.translatable("nobaaddons.name"))
				.category(GeneralCategory.create(defaults, config))
//            .category(VisualsCategory.create(defaults, config))
				.category(ChatCategory.create(defaults, config))
				.category(ChatCommandsCategory.create(defaults, config))
//            .category(MiscCategory.create(defaults, config))
		}.generateScreen(parent)
	}
}