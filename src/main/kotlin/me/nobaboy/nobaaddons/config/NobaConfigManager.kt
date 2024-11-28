package me.nobaboy.nobaaddons.config

import com.google.gson.FieldNamingPolicy
import dev.isxander.yacl3.api.YetAnotherConfigLib
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder
import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.config.categories.ChatCategory
import me.nobaboy.nobaaddons.config.categories.CrimsonIsleCategory
import me.nobaboy.nobaaddons.config.categories.DungeonsCategory
import me.nobaboy.nobaaddons.config.categories.EventsCategory
import me.nobaboy.nobaaddons.config.categories.FishingCategory
import me.nobaboy.nobaaddons.config.categories.GeneralCategory
import me.nobaboy.nobaaddons.config.categories.MiningCategory
import me.nobaboy.nobaaddons.config.categories.QOLCategory
import me.nobaboy.nobaaddons.config.categories.SlayersCategory
import me.nobaboy.nobaaddons.config.categories.UIAndVisualsCategory
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import java.lang.StackWalker.Option
import java.nio.file.Path

object NobaConfigManager {
	const val CONFIG_VERSION = 1
	private val CONFIG_DIR: Path = NobaAddons.modConfigDir.resolve("config.json")

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

	@get:JvmStatic
	val config get() = HANDLER.instance()

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
				.category(UIAndVisualsCategory.create(defaults, config))
				.category(EventsCategory.create(defaults, config))
				.category(SlayersCategory.create(defaults, config))
				.category(FishingCategory.create(defaults, config))
				.category(MiningCategory.create(defaults, config))
				.category(CrimsonIsleCategory.create(defaults, config))
				.category(DungeonsCategory.create(defaults, config))
				.category(ChatCategory.create(defaults, config))
				.category(QOLCategory.create(defaults, config))
		}.generateScreen(parent)
	}
}