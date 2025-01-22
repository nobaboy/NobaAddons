package me.nobaboy.nobaaddons.config

import dev.celestialfault.celestialconfig.AbstractConfig
import dev.isxander.yacl3.api.ButtonOption
import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.Controller
import dev.isxander.yacl3.api.LabelOption
import dev.isxander.yacl3.api.NameableEnum
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.OptionAddable
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.api.OptionEventListener
import dev.isxander.yacl3.api.OptionGroup
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder
import dev.isxander.yacl3.api.controller.ColorControllerBuilder
import dev.isxander.yacl3.api.controller.ControllerBuilder
import dev.isxander.yacl3.api.controller.DoubleSliderControllerBuilder
import dev.isxander.yacl3.api.controller.EnumControllerBuilder
import dev.isxander.yacl3.api.controller.FloatSliderControllerBuilder
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder
import dev.isxander.yacl3.api.controller.StringControllerBuilder
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder
import dev.isxander.yacl3.api.controller.ValueFormatter
import dev.isxander.yacl3.gui.YACLScreen
import dev.isxander.yacl3.gui.controllers.cycling.EnumController
import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.mixins.accessors.AbstractConfigAccessor
import me.nobaboy.nobaaddons.utils.ErrorManager
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.NobaColor.Companion.toNobaColor
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.minecraft.text.Text
import net.minecraft.util.PathUtil
import net.minecraft.util.TranslatableOption
import java.awt.Color
import java.nio.file.Path
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.io.path.nameWithoutExtension
import kotlin.reflect.KMutableProperty

object NobaConfigUtils {
	private val DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ROOT)

	/**
	 * Attempts to load the associated [AbstractConfig], logging an error and renaming the config file if it fails.
	 */
	fun AbstractConfig.safeLoad(pathSupplier: AbstractConfig.() -> Path = { (this as AbstractConfigAccessor).callGetPath() }) {
		try {
			load()
		} catch(ex: Throwable) {
			val path = pathSupplier(this)
			ErrorManager.logError("Failed to load a config file", ex)

			val date = DATE_FORMATTER.format(ZonedDateTime.now())
			val name = "${path.nameWithoutExtension}-${date}"
			val backup = PathUtil.getNextUniqueName(NobaAddons.CONFIG_DIR, name, ".json")

			if(path.toFile().renameTo(path.parent.resolve(backup).toFile())) {
				NobaAddons.LOGGER.warn("Moved config file to $backup")
			} else {
				NobaAddons.LOGGER.warn("Couldn't rename config file")
			}
		}
	}

	/**
	 * Attaches a [ClientLifecycleEvents] listener for when the client is stopping which calls [AbstractConfig.save]
	 */
	fun AbstractConfig.saveOnExit(onlyIfDirty: Boolean = false) {
		ClientLifecycleEvents.CLIENT_STOPPING.register {
			if(onlyIfDirty && !dirty) return@register
			try {
				save()
			} catch(ex: Throwable) {
				NobaAddons.LOGGER.error("Failed to save ${this::class.simpleName} before shutdown", ex)
			}
		}
	}

	fun createBooleanController(option: Option<Boolean>): BooleanControllerBuilder {
		return BooleanControllerBuilder.create(option).yesNoFormatter().coloured(true)
	}

	fun createTickBoxController(option: Option<Boolean>): TickBoxControllerBuilder {
		return TickBoxControllerBuilder.create(option)
	}

	@Suppress("UnstableApiUsage")
	fun <E : Enum<E>> createLimitedCyclerController(option: Option<E>, onlyInclude: Array<E>) = object : EnumControllerBuilder<E> {
		// I couldn't get EnumController.createDefaultFormatter() to work, so we're just reimplementing
		// this ourselves instead.
		private var formatter: ValueFormatter<E> = ValueFormatter<E> {
			when(it) {
				is NameableEnum -> it.displayName
				is TranslatableOption -> it.text
				else -> Text.literal(it.name)
			}
		}

		override fun enumClass(p0: Class<E>): EnumControllerBuilder<E> = throw UnsupportedOperationException()
		override fun formatValue(p0: ValueFormatter<E>): EnumControllerBuilder<E> = this.apply { formatter = p0 }
		override fun build(): Controller<E> = EnumController.createInternal(option, formatter, onlyInclude)
	}

	inline fun <reified E : Enum<E>> createCyclerController(option: Option<E>, onlyInclude: Array<E>? = null): EnumControllerBuilder<E> {
		if(onlyInclude != null) return createLimitedCyclerController(option, onlyInclude)
		return EnumControllerBuilder.create(option).enumClass(E::class.java)
	}

	fun createIntegerSliderController(option: Option<Int>, min: Int, max: Int, step: Int, format: ((Int) -> Text)? = null): ControllerBuilder<Int> {
		return IntegerSliderControllerBuilder.create(option).range(min, max).step(step).also { if(format != null) it.formatValue(format) }
	}

	fun createFloatSliderController(option: Option<Float>, min: Float, max: Float, step: Float, format: ((Float) -> Text)? = null): ControllerBuilder<Float> {
		return FloatSliderControllerBuilder.create(option).range(min, max).step(step).also { if(format != null) it.formatValue(format) }
	}

	fun createDoubleSliderController(option: Option<Double>, min: Double, max: Double, step: Double, format: ((Double) -> Text)? = null): ControllerBuilder<Double> {
		return DoubleSliderControllerBuilder.create(option).range(min, max).step(step).also { if(format != null) it.formatValue(format) }
	}

	fun createColorController(option: Option<Color>, allowAlpha: Boolean = false): ColorControllerBuilder {
		return ColorControllerBuilder.create(option).allowAlpha(allowAlpha)
	}

	fun createStringController(option: Option<String>): StringControllerBuilder {
		return StringControllerBuilder.create(option)
	}

	fun createLabelController(vararg lines: Text): LabelOption.Builder {
		require(lines.isNotEmpty()) { "Cannot create an empty label controller" }
		return LabelOption.createBuilder().apply {
			if(lines.size == 1) line(lines[0]) else lines(lines.toList())
		}
	}

	inline fun buildCategory(name: Text, builder: ConfigCategory.Builder.() -> Unit): ConfigCategory = ConfigCategory.createBuilder()
		.name(name)
		.apply(builder)
		.build()

	inline fun ConfigCategory.Builder.buildGroup(
		name: Text,
		description: Text? = null,
		collapsed: Boolean = true,
		crossinline builder: OptionGroup.Builder.() -> Unit
	) {
		group(OptionGroup.createBuilder()
			.name(name)
			.also { if(description != null) it.description(OptionDescription.of(description)) }
			.also { builder(it) }
			.collapsed(collapsed)
			.build())
	}

	fun <G : OptionAddable, T : Any> G.add(
		name: Text,
		description: Text? = null,
		optionController: (Option<T>) -> ControllerBuilder<T>,
		default: T,
		property: KMutableProperty<T>
	): Option<T> {
		return Option.createBuilder<T>()
			.name(name)
			.also { if(description != null) it.description(OptionDescription.of(description)) }
			.binding(default, property.getter::call, property.setter::call)
			.controller(optionController)
			.build()
			.also { option(it) }
	}

	fun <G : OptionAddable> G.boolean(
		name: Text,
		description: Text? = null,
		default: Boolean,
		property: KMutableProperty<Boolean>
	): Option<Boolean> {
		return add(name, description, ::createBooleanController, default, property)
	}

	fun <G : OptionAddable> G.tickBox(
		name: Text,
		description: Text? = null,
		default: Boolean,
		property: KMutableProperty<Boolean>
	): Option<Boolean> {
		return add(name, description, ::createTickBoxController, default, property)
	}

	fun <G : OptionAddable> G.string(
		name: Text,
		description: Text? = null,
		default: String,
		property: KMutableProperty<String>
	): Option<String> {
		return add(name, description, ::createStringController, default, property)
	}

	inline fun <G : OptionAddable, reified E : Enum<E>> G.cycler(
		name: Text,
		description: Text? = null,
		default: E,
		property: KMutableProperty<E>,
		onlyInclude: Array<E>? = null,
		formatter: ValueFormatter<E>? = null,
	): Option<E> {
		val builder: (Option<E>) -> ControllerBuilder<E> = {
			createCyclerController(it, onlyInclude).apply { if(formatter != null) formatValue(formatter) }
		}
		return add(name, description, builder, default, property)
	}

	@Suppress("UNCHECKED_CAST")
	inline fun <G : OptionAddable, reified N : Number> G.slider(
		name: Text,
		description: Text? = null,
		default: N,
		property: KMutableProperty<N>,
		min: N,
		max: N,
		step: N,
		noinline format: ((N) -> Text)? = null,
	): Option<N> {
		val controller: (Option<N>) -> ControllerBuilder<N> = when(N::class) {
			Integer::class -> { option ->
				createIntegerSliderController(option as Option<Int>, min.toInt(), max.toInt(), step.toInt(), format as ((Int) -> Text)?) as ControllerBuilder<N>
			}
			Float::class -> { option ->
				createFloatSliderController(option as Option<Float>, min.toFloat(), max.toFloat(), step.toFloat(), format as ((Float) -> Text)?) as ControllerBuilder<N>
			}
			Double::class -> { option ->
				createDoubleSliderController(option as Option<Double>, min.toDouble(), max.toDouble(), step.toDouble(), format as ((Double) -> Text)?) as ControllerBuilder<N>
			}
			else -> throw IllegalArgumentException("${N::class.java} does not have a slider controller")
		}

		return add(name, description, controller, default, property)
	}

	fun <G : OptionAddable> G.color(
		name: Text,
		description: Text? = null,
		default: Color,
		property: KMutableProperty<Color>,
		allowAlpha: Boolean = false,
	): Option<Color> {
		val option = Option.createBuilder<Color>()
			.name(name)
			.also { if(description != null) it.description(OptionDescription.of(description)) }
			.controller { createColorController(it, allowAlpha) }
			.binding(default, { property.getter.call() }) { property.setter.call(it) }
			.build()
		option(option)
		return option
	}

	fun <G : OptionAddable> G.color(
		name: Text,
		description: Text? = null,
		default: NobaColor,
		property: KMutableProperty<NobaColor>
	): Option<Color> {
		val option = Option.createBuilder<Color>()
			.name(name)
			.also { if(description != null) it.description(OptionDescription.of(description)) }
			.controller(::createColorController)
			.binding(default.toColor(), { property.getter.call().toColor() }) { property.setter.call(it.toNobaColor()) }
			.build()
		option(option)
		return option
	}

	fun <G : OptionAddable> G.label(vararg lines: Text): G = this.apply { option(createLabelController(*lines).build()) }

	fun <G : OptionAddable> G.button(name: Text, description: Text? = null, text: Text? = null, action: (YACLScreen) -> Unit): ButtonOption {
		val button = ButtonOption.createBuilder()
			.name(name)
			.also { if(description != null) it.description(OptionDescription.of(description)) }
			.also { if(text != null) it.text(text) }
			.action { screen, _ -> action(screen) }
			.build()
		option(button)
		return button
	}

	fun <T> Option<T>.availableIf(vararg listenTo: Option<*>, onlyIf: () -> Boolean): Option<T> {
		require(listenTo.isNotEmpty()) { "No options were provided to attach event listeners to" }
		if(available()) setAvailable(onlyIf())
		listenTo.forEach {
			it.addEventListener { _, type ->
				if(type != OptionEventListener.Event.AVAILABILITY_CHANGE) setAvailable(onlyIf())
			}
		}
		return this
	}

	infix fun <T> Option<T>.requires(other: Option<Boolean>): Option<T> {
		require(this !== other) { "Cannot make an option depend on itself" }
		return availableIf(other) { other.pendingValue() }
	}

	infix fun <T> Option<T>.requires(other: Collection<Option<Boolean>>): Option<T> {
		require(other.none { it === this }) { "Cannot make an option depend on itself" }
		return availableIf(*other.toTypedArray()) { other.all { it.pendingValue() } }
	}

	infix fun <T> Option<T>.requiresAny(other: Collection<Option<Boolean>>): Option<T> {
		require(other.none { it === this }) { "Cannot make an option depend on itself" }
		return availableIf(*other.toTypedArray()) { other.any { it.pendingValue() } }
	}

	fun <T, O> Option<T>.conflicts(other: Option<O>, onlyIf: (O) -> Boolean): Option<T> {
		require(this !== other) { "Cannot make an option conflict with itself" }
		return availableIf(other) { !onlyIf(other.pendingValue()) }
	}

	infix fun <T> Option<T>.conflicts(other: Option<Boolean>): Option<T> = conflicts(other) { it }

	infix fun <T> Option<T>.conflicts(other: Collection<Option<Boolean>>): Option<T> {
		require(other.none { it === this }) { "Cannot make an option conflict with itself" }
		return availableIf(*other.toTypedArray()) { other.none { it.pendingValue() } }
	}
}