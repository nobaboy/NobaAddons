package me.nobaboy.nobaaddons.features

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.serializer
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.jvm.isAccessible

@MustBeDocumented
@Target(AnnotationTarget.PROPERTY)
annotation class Option(val key: String = "")

class WrappedOption<T> internal constructor(private val instance: Any?, private val property: KMutableProperty<T>) {
	init {
		property.isAccessible = true
		require(property.hasAnnotation<Option>()) { "Property must be annotated with @Option" }
	}

	val name = property.findAnnotation<Option>()?.key?.takeIf { it.isNotBlank() } ?: property.name
	val default: T = get()

	@Suppress("UNCHECKED_CAST")
	val serializer = serializer(property.returnType) as KSerializer<T>

	fun get(): T {
		val instanceParam = property.getter.parameters.firstOrNull { it.kind == KParameter.Kind.INSTANCE }
		return property.getter.callBy(buildMap {
			instanceParam?.let { put(it, instance) }
		})
	}

	fun set(value: T) {
		val instanceParam = property.setter.parameters.firstOrNull { it.kind == KParameter.Kind.INSTANCE }
		val valueParam = property.setter.parameters.first { it.kind == KParameter.Kind.VALUE }
		property.setter.callBy(buildMap {
			instanceParam?.let { put(it, instance) }
			put(valueParam, value)
		})
	}

	fun get(json: Json): JsonElement {
		return json.encodeToJsonElement(serializer, get())
	}

	fun set(json: Json, element: JsonElement) {
		try {
			set(json.decodeFromJsonElement(serializer, element))
		} catch(_: SerializationException) {
		} catch(_: IllegalArgumentException) {
		}
	}
}
