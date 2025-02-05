package me.nobaboy.nobaaddons.config.option

import dev.isxander.yacl3.api.ConfigCategory
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlin.collections.iterator
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

abstract class AbstractConfigOptionHolder : ConfigOptionHolder {
	protected val options: Map<String, ConfigOption<*>> by lazy {
		this::class.memberProperties
			.sortedBy { it.findAnnotation<Order>()?.order ?: 0 }
			.mapNotNull {
				it as? KMutableProperty1<AbstractConfigOptionHolder, *> ?: return@mapNotNull null
				it.isAccessible = true
				val delegate = it.getDelegate(this@AbstractConfigOptionHolder) as? ConfigOption<*> ?: return@mapNotNull null
				it.name to delegate
			}
			.toMap()
	}

	fun load(json: Json, obj: JsonObject) {
		val config = obj["config"] as? JsonObject ?: JsonObject(emptyMap())
		for((name, option) in options) {
			option.set(json, config[name] ?: continue)
		}
	}

	fun dump(json: Json): JsonObject = buildJsonObject {
		put("config", buildJsonObject {
			for ((name, option) in options) {
				put(name, option.get(json))
			}
		})
	}

	protected inline fun <reified T> config(default: T, builder: OptionBuilder<T>.() -> Unit = {}): ConfigOption<T> =
		buildOption(this) {
			this.default = default
			builder(this)
		}

	protected inline fun <reified T> config(builder: OptionBuilder<T>.() -> Unit): ConfigOption<T> =
		buildOption(this, builder)

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