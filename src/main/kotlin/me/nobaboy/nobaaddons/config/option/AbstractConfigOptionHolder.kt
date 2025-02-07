package me.nobaboy.nobaaddons.config.option

import dev.isxander.yacl3.api.ConfigCategory
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.serializer
import kotlin.collections.iterator
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

abstract class AbstractConfigOptionHolder(val id: String) : ConfigOptionHolder, Iterable<ConfigOption<*>> {
	protected open val migrations: ConfigOptionMigration? = null

	protected val options: Map<String, ConfigOption<*>> by lazy {
		this::class.memberProperties
			.sortedBy { it.findAnnotation<Order>()?.order ?: 0 }
			.mapNotNull {
				it as? KMutableProperty1<AbstractConfigOptionHolder, *> ?: return@mapNotNull null
				it.name to (it.getOptionDelegate(this) ?: return@mapNotNull null)
			}
			.toMap()
	}

	override fun iterator(): Iterator<ConfigOption<*>> = options.values.iterator()

	@Suppress("UNCHECKED_CAST")
	protected fun <I, T> KMutableProperty1<I, T>.getOptionDelegate(instance: I): ConfigOption<T>? {
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
	fun load(json: Json, obj: JsonObject) {
		migrations?.apply(json, obj)
		val config = obj["config"] as? JsonObject ?: JsonObject(emptyMap())
		for((name, option) in options) {
			option.set(json, config[name] ?: continue)
		}
	}

	/**
	 * Dump all current [ConfigOption]s to a [JsonObject]
	 */
	fun dump(json: Json): JsonObject = buildJsonObject {
		put("config", buildJsonObject {
			for ((name, option) in options) {
				put(name, option.get(json))
			}
		})
	}

	open fun onSave() {
		options.values.forEach(ConfigOption<*>::saveEvent)
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

	/**
	 * Implement your YACL config building here.
	 */
	abstract fun buildConfig(category: ConfigCategory.Builder)

	override operator fun get(key: String): ConfigOption<*>? = options[key]

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