package me.nobaboy.nobaaddons.features

import dev.isxander.yacl3.api.OptionGroup
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import me.nobaboy.nobaaddons.events.Event
import me.nobaboy.nobaaddons.events.EventDispatcher
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties

abstract class Feature(val id: String, val category: FeatureCategory) {
	val killswitch by KillSwitch(null)

	private val options: Map<String, WrappedOption<*>> by lazy {
		this::class.memberProperties
			.mapNotNull { it as? KMutableProperty<*> }
			.filter { it.hasAnnotation<Option>() }
			.map { WrappedOption(this, it) }
			.associate { it.name to it }
	}

	/**
	 * Returns the [WrappedOption] for the given [Option] annotated property
	 */
	@Suppress("UNCHECKED_CAST")
	protected fun <T> KMutableProperty<T>.option(): WrappedOption<T> {
		val name = this.findAnnotation<Option>()?.key?.takeIf { it.isNotBlank() } ?: name
		println(name)
		return options[name] as WrappedOption<T>
	}

	internal fun load(json: Json, obj: JsonObject) {
		val config = obj["config"] as? JsonObject ?: JsonObject(emptyMap())
		for(option in options.values) {
			option.set(json, config[option.name] ?: continue)
		}
	}

	internal fun dump(json: Json): JsonObject = buildJsonObject {
		put("config", buildJsonObject {
			for(option in options.values) {
				put(option.name, option.get(json))
			}
		})
	}

	/**
	 * Registers the given [listener] on the given [dispatcher], only invoking it if this feature's [killswitch] hasn't
	 * been activated.
	 */
	protected fun <T : Event> listen(dispatcher: EventDispatcher<T>, listener: (T) -> Unit) {
		dispatcher.register { if(!killswitch) listener(it) }
	}

	open fun init() {
	}

	open fun config(): OptionGroup? = null
}