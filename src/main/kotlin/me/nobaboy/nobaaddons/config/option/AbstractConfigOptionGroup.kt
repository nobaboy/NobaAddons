package me.nobaboy.nobaaddons.config.option

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.serializer
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

/**
 * Generic abstract implementation of [ConfigOptionGroup]
 */
abstract class AbstractConfigOptionGroup(val id: String) : ConfigOptionGroup, Iterable<Config> {
	private val unaccepted = mutableMapOf<String, JsonElement>()

	override val options: Map<String, Config> by lazy {
		this::class.memberProperties
			.sortedBy { it.findAnnotation<Order>()?.order ?: 0 }
			.mapNotNull {
				it as? KProperty1<AbstractConfigOptionGroup, *> ?: return@mapNotNull null
				it.name to (it.getOptionDelegate(this) ?: return@mapNotNull null)
			}
			.toMap()
	}

	@Suppress("UNCHECKED_CAST")
	protected fun <I, T> KProperty1<I, T>.getOptionDelegate(instance: I): ConfigOption<T>? {
		try {
			isAccessible = true
		} catch(_: Error) {
			// kotlin.reflect.jvm.internal.KotlinReflectionInternalError: Inconsistent number of parameters in the descriptor and Java reflection object: 0 != 1
			// Calling: private final fun `<get-lastAlert>`(): me.nobaboy.nobaaddons.utils.Timestamp defined in me.nobaboy.nobaaddons.features.slayers.MiniBossFeatures[PropertyGetterDescriptorImpl@319df56b]
			// Parameter types: [])
			return null
		}
		return getDelegate(instance) as? ConfigOption<T>
	}

	/**
	 * Load configs for all known [ConfigOption]s from the provided [JsonObject]
	 */
	override fun load(json: Json, obj: JsonObject) {
		for((key, value) in obj) {
			val option = this[key]
			if(option == null) {
				unaccepted[key] = value
				continue
			}
			when(option) {
				is ConfigOption<*> -> option.set(json, value)
				is ConfigOptionGroup -> option.load(json, value as? JsonObject ?: continue)
				else -> error("Expected ConfigOption<*> or ConfigOptionGroup, got ${option::class.simpleName} instead")
			}
		}
	}

	/**
	 * Dump all current [ConfigOption]s to a [JsonObject]
	 */
	override fun dump(json: Json): JsonObject = buildJsonObject {
		unaccepted.forEach(::put)
		for((name, option) in options) {
			put(name, when(option) {
				is ConfigOption<*> -> option.get(json)
				is ConfigOptionGroup -> option.dump(json)
				else -> error("Expected ConfigOption<*> or ConfigOptionGroup, got ${option::class.simpleName} instead")
			})
		}
	}

	/**
	 * Create a new [ConfigOption] with the given [default] value
	 */
	protected inline fun <reified T> config(
		default: T,
		serializer: KSerializer<T> = serializer<T>(),
		builder: OptionBuilder<T>.() -> Unit = {}
	): ConfigOption<T> =
		config<T>(serializer) {
			this.default = default
			builder(this)
		}

	/**
	 * Create a new [ConfigOption]
	 */
	protected inline fun <reified T> config(
		serializer: KSerializer<T> = serializer<T>(),
		builder: OptionBuilder<T>.() -> Unit
	): ConfigOption<T> {
		val optionBuilder = OptionBuilder<T>(this, serializer)
		builder(optionBuilder)
		return optionBuilder.build()
	}

	override fun saveEvent() {
		options.values.forEach(Config::saveEvent)
	}

	override fun get(key: String): Config? = options[key]
	override fun iterator(): Iterator<Config> = options.values.iterator()

	fun dumpUnaccepted() {
		unaccepted.clear()
		options.values.forEach {
			if(it is AbstractConfigOptionGroup) it.dumpUnaccepted()
		}
	}

	/**
	 * Annotate on a [config] field to change the order that it appears in the built YACL config.
	 *
	 * Defaults to `0` if no annotation is present.
	 *
	 * @see sortedBy
	 */
	@Target(AnnotationTarget.PROPERTY)
	protected annotation class Order(val order: Int)
}

private fun <T> ConfigOption<T>.get(json: Json): JsonElement = json.encodeToJsonElement(serializer, get())
private fun <T> ConfigOption<T>.set(json: Json, element: JsonElement) = set(json.decodeFromJsonElement(serializer, element))